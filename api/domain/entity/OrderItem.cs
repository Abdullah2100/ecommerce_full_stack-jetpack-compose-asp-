using System.ComponentModel.DataAnnotations;

namespace api.domain.entity;

public enum EnOrderItemStatus {Cancelled,InProgress,Excepted,ReceivedByDelivery }

public class OrderItem:GeneralShredInfo
{
    public Guid OrderId { get; set; }
    public Guid ProductId { get; set; }
    public decimal Price { get; set; }
    public int Quantity { get; set; }
    public Guid StoreId { get; set; }
    public Order  Order { get; set; }
    public Store Store { get; set; }
    public Product Product { get; set; }
    public ICollection<OrderProductsVariant>? OrderProductsVariants { get; set; } = null;
    public EnOrderItemStatus Status { get; set; }= EnOrderItemStatus.InProgress;
}