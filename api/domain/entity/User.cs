using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;


public class User
{
    [Key] public Guid Id { get; set; }
    public string Name { get; set; }
    public string Phone { get; set; }
    public string  Email{ get; set; }
    public string Password { get; set; }
    public bool IsBlocked { get; set; } = false;
    public string? DeviceToken { get; set; } = null;
    
    //1 :normal user ; 0: is admin
    public int Role { get; set; } = 1;

    
    [Column(TypeName = "Timestamp")]
     public DateTime CreatedAt { get; set; } = DateTime.Now;

     
    [Column(TypeName = "Timestamp")]
     public DateTime? UpdatedAt { get; set; } = null;
    
    public string? Thumbnail { get; set; }
    public ICollection<Address>? Addresses { get; set; }
    public ICollection<Category>? Categories { get; set; }
    public ICollection<Order>? Orders { get; set; }

    public Store? Store { get; set; } = null;
    public Delivery? Delivery { get; set; } = null;
}