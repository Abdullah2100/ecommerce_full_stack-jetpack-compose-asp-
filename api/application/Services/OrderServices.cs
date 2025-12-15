using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;
using api.shared.signalr;
using api.util;
using ecommerc_dotnet.midleware.ConfigImplment;
using Microsoft.AspNetCore.SignalR;

namespace api.application.Services;

public class OrderServices(
    IUnitOfWork unitOfWork,
    IConfig config,
    IMessageService messageService,
    IHubContext<OrderHub> hubContext)
    : IOrderServices
{
    public static readonly List<string> OrderStatus = new List<string>
    {
        "Rejected",
        "Inprogress",
        "Accepted",
        "In away",
        "Received",
        "Completed",
    };


    public async Task<Result<OrderDto?>> CreateOrder(Guid userId, CreateOrderDto orderDto)
    {
        User? user = await unitOfWork.UserRepository.GetUser(userId);

        var isValidated = user.IsValidateFunc(isAdmin: false);

        if (isValidated is not null)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: isValidated.Message,
                isSuccessful: false,
                statusCode: isValidated.StatusCode
            );
        }

        if (!(await unitOfWork.OrderRepository.IsValidTotalPrice(orderDto.TotalPrice, orderDto.Items,orderDto.Symbol)))
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "order totalPrice is not valid",
                isSuccessful: false,
                statusCode: 400
            );
        }

        var id = ClsUtil.GenerateGuid();
        Order? order = new Order
        {
            Id = id,
            Longitude = orderDto.Longitude,
            Latitude = orderDto.Latitude,
            UserId = userId,
            TotalPrice = orderDto.TotalPrice,
            Status = 1,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
            Symbol = orderDto.Symbol
        };

        unitOfWork.OrderRepository.Add(order);

        foreach (var item in orderDto.Items)
        {
            var orderItemId = ClsUtil.GenerateGuid();
            List<OrderProductsVariant>? orderProductsVariants =
                item.ProductVariant == null || item.ProductVariant.Count == 0
                    ? null
                    : item.ProductVariant
                        .Select(x => new OrderProductsVariant
                        {
                            Id = ClsUtil.GenerateGuid(),
                            OrderItemId = orderItemId,
                            ProductVariantId = x
                        })
                        .ToList();

            OrderItem orderItem = new OrderItem
            {
                Id = orderItemId,
                OrderId = id,
                ProductId = item.ProductId,
                Quantity = item.Quantity,
                StoreId = item.StoreId,
                Price = item.Price,
            };
            unitOfWork.OrderItemRepository.Add(orderItem);
            
            if (orderProductsVariants is not null)
                unitOfWork.OrderProductVariantRepository.Add(orderProductsVariants);
        }


        int result = await unitOfWork.SaveChanges();


        if (result == 0)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "error while create order",
                isSuccessful: false,
                statusCode: 400
            );
        }

        var isSavedDistance = await unitOfWork.OrderRepository.IsSavedDistanceToOrder(order.Id);
        // this line to save delete order if is deleted
        await unitOfWork.SaveChanges();
        if (isSavedDistance == false)
        {
            
            return new Result<OrderDto?>
            (
                data: null,
                message: "could not calculate  distance distance to user ",
                isSuccessful: false,
                statusCode: 400
            );
        }

        order = await unitOfWork.OrderRepository.GetOrder(order.Id);
        if (order is null)
        {
            return new Result<OrderDto?>
            (
                data: null,
                message: "error while create order",
                isSuccessful: false,
                statusCode: 400
            );
        }

        var dtoOrder = order.ToDto(config.getKey("url_file"));
        await hubContext.Clients.All.SendAsync("createdOrder", dtoOrder);
        await SendNotification(order, 1);


        return new Result<OrderDto?>
        (
            data: dtoOrder,
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }


    public async Task<Result<List<OrderDto>>> GetMyOrders(Guid userId, int pageNum, int pageSize)
    {
        List<OrderDto> orders = (await unitOfWork.OrderRepository
                .GetOrders(userId, pageNum, pageSize))
            .Select(o => o.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderDto>>
        (
            data: orders,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    //for admin dashboard
    public async Task<Result<AdminOrderDto?>> GetOrders(Guid userId, int pageNum, int pageSize)
    {
        User? delivery = await unitOfWork.UserRepository.GetUser(userId);

        var isValid = delivery.IsValidateFunc(true);

        if (isValid is not null)
        {
            return new Result<AdminOrderDto?>
            (
                data: null,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        List<OrderDto> orders = (await unitOfWork.OrderRepository
                .GetOrders(pageNum, pageSize))
            .Select(o => o.ToDto(config.getKey("url_file")))
            .ToList();

        int orderPages = (int)Math.Ceiling((double)orders.Count / pageSize);

        var holder = new AdminOrderDto { Orders = orders, pageNum = orderPages };
        return new Result<AdminOrderDto?>
        (
            data: holder,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> UpdateOrderStatus(Guid id, int status)
    {
        Order? order = await unitOfWork.OrderRepository
            .GetOrder(id);

        if (order is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        order.Status = status;

        unitOfWork.OrderRepository.Update(order);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while update order status",
                isSuccessful: false,
                statusCode: 400
            );
        }

        await hubContext.Clients.All.SendAsync("orderStatus", new UpdateOrderStatusEventDto
        {
            Id = order.Id,
            Status = OrderStatus[status]
        });

        //this for notification operation for all user at the system

        await SendNotification(order, status);
        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<bool>> DeleteOrder(Guid id, Guid userId)
    {
        Order? order = await unitOfWork.OrderRepository.GetOrder(id, userId);
        if (order is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order not found ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        unitOfWork.OrderRepository.Delete(id);
        int result = await unitOfWork.SaveChanges();
        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete order",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }


    // for delivery 
    public async Task<Result<List<OrderDto>>> GetOrdersByDeliveryId(Guid deliveryId, int pageNum, int pageSize)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository.GetDelivery(deliveryId);

        var isValid = delivery.IsValidated();

        if (isValid is not null)
        {
            return new Result<List<OrderDto>>
            (
                data: new List<OrderDto>(),
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        List<OrderDto> orders = (await unitOfWork.OrderRepository
                .GetOrderBelongToDelivery(deliveryId, pageNum, pageSize))
            .Select(o => o.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderDto>>
        (
            data: orders,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<OrderDto>>> GetOrdersNotBelongToDeliveries(Guid deliveryId, int pageNum, int pageSize)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository.GetDelivery(deliveryId);

        var isValid = delivery.IsValidated();
        if (isValid is not null)
        {
            return new Result<List<OrderDto>>
            (
                data: new List<OrderDto>(),
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        List<OrderDto> orders = (await unitOfWork.OrderRepository
                .GetOrderNoBelongToAnyDelivery(pageNum, pageSize))
            .Select(o => o.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderDto>>
        (
            data: orders,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<bool>> SubmitOrderToDelivery(Guid id, Guid deliveryId)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository.GetDelivery(deliveryId);

        var isValid = delivery.IsValidated();

        if (isValid is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }


        Order? order = await unitOfWork.OrderRepository.GetOrder(id);

        if (order == null)
        {
            return new Result<bool>
            (
                data: false,
                message: "Order not exists",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (order.DeliveryId != null)
            return new Result<bool>
            (
                data: false,
                message: "Order Delivered By another Delivery",
                isSuccessful: false,
                statusCode: 404
            );


        order.DeliveryId = deliveryId;
        order.UpdatedAt = DateTime.Now;

        unitOfWork.OrderRepository.Update(order);

        var result = await unitOfWork.SaveChanges();


        if (result < 1)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while update order",
                isSuccessful: false,
                statusCode: 400
            );
        }

        OrderTookByEvent eventHolder = new OrderTookByEvent
        {
            Id = id,
            DeliveryId = deliveryId
        };

        await hubContext.Clients.All.SendAsync("orderGettingByDelivery", eventHolder);
        await hubContext.Clients.All.SendAsync("orderStatus", new UpdateOrderStatusEventDto
        {
            Id = order.Id,
            Status = OrderStatus[2]
        });


        await SendNotification(order, status: 2);
        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<bool>> CancelOrderFromDelivery(Guid id, Guid deliveryId)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository.GetDelivery(deliveryId);

        var isValid = delivery.IsValidated();
        if (isValid is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        Order? order = await unitOfWork.OrderRepository.GetOrder(id);

        if (order is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "order not found ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (!(await unitOfWork.OrderRepository.IsCanCancelOrder(id)))
        {
            return new Result<bool>
            (
                data: false,
                message: "order can not cancel some orderitems recived from stores by delivery ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        unitOfWork.OrderRepository.RemoveOrderFromDelivery(id, deliveryId);
        int result = await unitOfWork.SaveChanges();


        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while remove order from delivery",
                isSuccessful: false,
                statusCode: 400
            );
        }

        await hubContext.Clients.All.SendAsync("createdOrder", order.ToDto(config.getKey("url_file")));

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<List<string>>> GetOrdersStatus(Guid adminId)
    {
        User? user = await unitOfWork.UserRepository.GetUser(adminId);
        var isValide = user.IsValidateFunc();

        if (isValide is not null)
        {
            return new Result<List<string>>(
                data: new List<string>(),
                message: isValide.Message,
                isSuccessful: false,
                statusCode: isValide.StatusCode
            );
        }

        return new Result<List<string>>(
            data: OrderStatus,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    private async Task SendNotification(Order order, int status)
    {
        await SendNotificationToStore(order, status);
        await SendNotificationToUser(order, status);
        await SendNotificationToDelivery(order, status);
    }

    private async Task SendNotificationToStore(Order order, int status)
    {
        try
        {
            var messageServe = new SendMessageServices(new NotificationServices());

            var orderItems = order.Items.ToList();

            for (int i = 0; i < orderItems.Count; i++)
            {
                var orderItem = orderItems[i];
                var cancelMessage = orderItem.Product.Name + " is Rejected For " + order.User.Name;
                var storeMessage = this.StoreMessage(status, cancelMessage);
                if (!string.IsNullOrEmpty(storeMessage))
                {
                    await messageServe.SendMessage(storeMessage, orderItem.Store.user.deviceToken);
                }
            }
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error from notification service: {e.Message}");
        }
    }

    private async Task SendNotificationToUser(Order order, int status)
    {
        try
        {
            var messageServe = new SendMessageServices(new NotificationServices());

            var userMessage = this.UserMessage(status);
            if (!string.IsNullOrEmpty(userMessage))
            {
                await messageServe.SendMessage(userMessage, order.User.deviceToken);
            }
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error from notification service: {e.Message}");
        }
    }

    private async Task SendNotificationToDelivery(Order order, int status)
    {
        try
        {
            var messageServe = new SendMessageServices(new NotificationServices());

            var deliveryMessage = this.DeliveryMessage(status);

            Delivery? delivery = null;
            if (order.DeliveryId is not null)
            {
                delivery = await unitOfWork.DeliveryRepository.GetDelivery(order.DeliveryId ?? Guid.Empty);
            }

            switch (status)
            {
                case 0:
                {
                    await messageServe.SendMessage(deliveryMessage, delivery.DeviceToken);
                }
                    break;

                case 1:
                {
                    await SendNotificationToDeliveries(deliveryMessage, messageServe);
                }
                    break;

                case 5:
                {
                    await messageServe.SendMessage(deliveryMessage, delivery.DeviceToken);
                }
                    break;
            }
        }
        catch
            (Exception e)
        {
            Console.WriteLine($"Error from notification service: {e.Message}");
        }
    }

    private async Task SendNotificationToDeliveries(
        string message,
        SendMessageServices messageServe)
    {
        try
        {
            var deliveriesLenght = await unitOfWork.DeliveryRepository.GetDeliveriesPage(20);
            for (int i = 0; i < deliveriesLenght; i++)
            {
                var deliveryList = await unitOfWork.DeliveryRepository.GetDeliveries(i + 1, 20);
                if (deliveryList is null) continue;
                foreach (var delivery in deliveryList)
                {
                    if (delivery.DeviceToken is not null)
                        await messageService.SendingMessage(message, delivery.DeviceToken!);
                }
            }
        }
        catch
            (Exception e)
        {
            Console.WriteLine($"Error from notification service: {e.Message}");
        }
    }

    private string UserMessage(int status)
    {
        return status switch
        {
            0 => "Your Order is Rejected",
            1 => "Your Order is Submit Successful",
            2 => "Your Order is Accepted By Delivery Man",
            3 => "Your Order in Away to Your Place",
            4 => "Your Order is Received",
            5 => "Your Order is Delivered",
            _ => ""
        };
    }

    private string DeliveryMessage(int status)
    {
        return status switch
        {
            0 => "Order is Rejected",
            1 => "New Order is Submit",
            5 => "Your Order is Received",
            _ => ""
        };
    }

    private string StoreMessage(int status, string customMessage = "")
    {
        return status switch
        {
            0 => customMessage,
            2 => "There Are New Order For Your Store Check them",
            5 => "Your Order is Delivered",
            _ => ""
        };
    }
}