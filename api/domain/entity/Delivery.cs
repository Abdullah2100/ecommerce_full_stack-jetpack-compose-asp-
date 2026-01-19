using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Delivery : GeneralShredInfo
{
    public Guid UserId { get; set; }
    public string? DeviceToken { get; set; } = null;
    public bool IsAvailable { get; set; } = true;
    public bool IsBlocked { get; set; } = false;
    public string? Thumbnail { get; set; }
    public Guid? BelongTo { get; set; } = null;
    public Address? Address { get; set; }
    public User User { get; set; }
    public ICollection<Order>? Orders { get; set; }
}