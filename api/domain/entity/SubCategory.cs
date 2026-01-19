using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class SubCategory:GeneralShredInfo
{
    public string Name { get; set; }
    public Guid StoreId { get; set; }
    public Store? Store { get; set; }
    public Guid CategoryId { get; set; }
    public Category? Category { get; set; } 
    public ICollection<Product>? Products { get; set; }
}