using api.domain.entity;
using api.Presentation.dto;

namespace api.domain.Interface;

public interface IOrderRepository:IRepository<Order>
{
    Task<IEnumerable<Order>> GetOrders(Guid userId,int pageNum,int pageSize);
    Task<IEnumerable<Order>> GetOrders(int page,int lenght);
    
    Task<Order?> GetOrder(Guid id);
    
    Task<Order?> GetOrder(Guid id,Guid  userId);
    
    Task<bool> IsExist(Guid id);
    Task<bool> IsCanCancelOrder(Guid id);
    Task<bool> IsValidTotalPrice(decimal totalPrice,List<CreateOrderItemDto> items,string symbol);
    
    //delivery
    Task<IEnumerable<Order>> GetOrderNoBelongToAnyDelivery(int pageNum,int pageSize);
    Task<IEnumerable<Order>> GetOrderBelongToDelivery(Guid deliveryId,int pageNum,int pageSize);
    void  RemoveOrderFromDelivery(Guid id,Guid deliveryId);
    Task<bool> IsSavedDistanceToOrder(Guid id);
    void Delete(Guid id);
    
}