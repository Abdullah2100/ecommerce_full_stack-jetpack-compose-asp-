using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;


public class User:GeneralShredInfo
{
    public string Name { get; set; }
    public string Phone { get; set; }
    public string  Email{ get; set; }
    public string Password { get; set; }
    public bool IsBlocked { get; set; } = false;
    public string? DeviceToken { get; set; } = null;
    //1 :normal user ; 0: is admin
    public bool  IsUser { get; set; } = true;
    public string? Thumbnail { get; set; }
    public ICollection<Address>? Addresses { get; set; }
    public ICollection<Category>? Categories { get; set; }
    public ICollection<Order>? Orders { get; set; }
    public ICollection<PaymentType>? PaymentTypes { get; set; } = null; // this for admin adding many payement type

    public Store? Store { get; set; } = null;
    public Delivery? Delivery { get; set; } = null;
}