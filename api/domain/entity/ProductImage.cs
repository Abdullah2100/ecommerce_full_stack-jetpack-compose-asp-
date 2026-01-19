using System.ComponentModel.DataAnnotations;

namespace api.domain.entity;

public class ProductImage:GeneralSharedInfoWithId
{
    public string Path { get; set; }
    public Guid ProductId { get; set; }
    public Product  Product { get; set; }
}