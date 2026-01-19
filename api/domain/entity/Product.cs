using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Product:GeneralShredInfo
{
    public string Name { get; set; }
    public string Description { get; set; }
    public string Thumbnail { get; set; }
    public Guid SubcategoryId { get; set; }
    public Guid StoreId { get; set; }
    public int Price { get; set; }
    public int? Quantity { get; set; } = null;
    public String Symbol { get; set; }
    public ICollection<ProductVariant>? ProductVariants { get; set; } = null;
    public ICollection<ProductImage>? ProductImages { get; set; } = null;
    public SubCategory SubCategory { get; set; }
    public Store Store { get; set; }
}