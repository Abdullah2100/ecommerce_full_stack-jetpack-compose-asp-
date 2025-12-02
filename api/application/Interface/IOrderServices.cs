using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface IOrderServices
{
    Task<Result<OrderDto?>> CreateOrder(Guid userId,CreateOrderDto orderDto);
    Task<Result<List<OrderDto>>> GetMyOrders(Guid userId,int pageNum,int pageSize);
    
    //order for admin
    Task<Result<AdminOrderDto?>> GetOrders(Guid userId,int pageNum,int pageSize);

    Task<Result<bool>> UpdateOrderStatus(Guid id, int status);
    
   Task<Result<bool>> DeleteOrder(Guid id,Guid userId);
   
   //delivery 
   Task<Result<List<OrderDto>>> GetOrdersByDeliveryId(Guid deliveryId,int pageNum,int pageSize);
   Task<Result<List<OrderDto>>> GetOrdersNotBelongToDeliveries(Guid deliveryId,int pageNum,int pageSize);
   Task<Result<bool>> SubmitOrderToDelivery(Guid id,Guid deliveryId);
   Task<Result<bool>> CancelOrderFromDelivery(Guid id,Guid deliveryId);
   Task<Result<List<string>>> GetOrdersStatus(Guid adminId);
}