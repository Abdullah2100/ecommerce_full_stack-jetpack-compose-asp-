using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Product
{
    public Guid Id { get; set; }
    public string Name { get; set; }
    
    [Column(TypeName = "TEXT")]
    public string Description { get; set; }
    public string  Thumbnail { get; set; }
    public Guid SubcategoryId { get; set; }
    public Guid StoreId { get; set; }
    public decimal Price { get; set; }
    
    public int? Quantity { get; set; } = null;
    
    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; }

    [Column(TypeName = "Timestamp")] 
    public DateTime? UpdatedAt { get; set; } = null;
    
    public String Symbol { get; set; } 

    public ICollection<ProductVariant>? ProductVariants { get; set; } = null;
    public ICollection<ProductImage>? ProductImages { get; set; } = null;
    
   public SubCategory SubCategory { get; set; }
   public Store Store { get; set; }
}