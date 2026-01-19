using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Store:GeneralShredInfo
{
    public string Name { get; set; }
    public string WallpaperImage  { get; set; }
    public string  SmallImage { get; set; }
    public bool IsBlock { get; set; } = true;
    public Guid UserId { get; set; }
    public ICollection<Address>? Addresses { get; set; } =null;
    public ICollection<SubCategory>? SubCategories { get; set; } =null;
    public ICollection<Banner>? Banners { get; set; } = null;
    public ICollection<Product>? Products { get; set; } = null;
    public ICollection<OrderItem>? OddrderItems { get; set; } = null;

    public User user {get; set;}
}