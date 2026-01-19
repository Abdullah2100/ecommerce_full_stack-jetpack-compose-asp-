using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;
using api.shared.signalr;
using ecommerc_dotnet.midleware.ConfigImplment;
using Microsoft.AspNetCore.SignalR;

namespace api.application.Services;

public class OrderItemServices(
    IConfig config,
    IHubContext<OrderItemHub> hubContext,
    IUnitOfWork unitOfWork,
    IOrderServices orderServices
)
    : IOrderItemServices
{
    public async Task<Result<List<OrderItemDto>>> GetOrderItmes(
        Guid storeId,
        int pageNum,
        int pageSize)
    {
        User? user = await unitOfWork.UserRepository.GetUser(storeId);

        var isValidate = user.IsValidateFunc(isAdmin: false, isStore: true);
        if (isValidate is not null)
        {
            return new Result<List<OrderItemDto>>(
                data: new List<OrderItemDto>(),
                message: isValidate.Message,
                isSuccessful: false,
                statusCode: isValidate.StatusCode
            );
        }

        List<OrderItemDto> orderItems = (await unitOfWork.OrderItemRepository
                .GetOrderItems(storeId: user.Store.Id, pageNum: pageNum, pageSize: pageSize))
            .Select(p => p.ToOrderItemDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<OrderItemDto>>
        (
            data: orderItems,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<int>> UpdateOrderItmesStatus(
        Guid userId,
        UpdateOrderItemStatusDto orderItemsStatusDto)
    {
        OrderItem? orderItem = await unitOfWork.OrderItemRepository.GetOrderItem(orderItemsStatusDto.Id);

        if (orderItem is null)
        {
            return new Result<int>
            (
                data: 0,
                message: "orderItem not found",
                isSuccessful: false,
                statusCode: 404
            );
        } ;
        
        orderItem.Status = orderItemsStatusDto.Status == EnOrderItemStatusDto.Excepted
            ? EnOrderItemStatus.Excepted
            : orderItemsStatusDto.Status == EnOrderItemStatusDto.TookByDelivery
                ? EnOrderItemStatus.ReceivedByDelivery
                : EnOrderItemStatus.Cancelled;
        
        unitOfWork.OrderItemRepository.Update(orderItem);
        
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<int>
            (
                data: 0,
                message: "error while update orderItme status",
                isSuccessful: false,
                statusCode: 400
            );
        }

        OrderItemsStatusEvent statusEvent = new OrderItemsStatusEvent
        {
            OrderId = orderItem.OrderId,
            OrderItemId = orderItem.Id,
            Status = orderItem.Status.ToString()
        };
        await hubContext.Clients.All.SendAsync("orderItemsStatusChange", statusEvent);

       

        return new Result<int>
        (
            data: 1,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

 

}