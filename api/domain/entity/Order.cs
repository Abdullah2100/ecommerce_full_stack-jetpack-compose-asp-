using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Order:GeneralShredInfo
{
    public decimal Longitude { get; set; }
    public decimal Latitude { get; set; }
    public Guid UserId { get; set; }
    public long TotalPrice { get; set; }
    public string Symbol { get; set; }
    public int Status { get; set; }
    public int DistanceToUser { get; set; } = 0;
    public int DistanceFee { get; set; } = 0;
    public bool IsFail {get; set;} = false;
    public Guid PaymentTypeId { get; set; }
    public Guid? DeliveryId { get; set; } = null;
    public PaymentType PaymentType { get; set; }
    public User User { get; set; }
    public Delivery? DeliveredBy { get; set; } = null;
    public ICollection<OrderItem> Items { get; set; }=new List<OrderItem>();
}