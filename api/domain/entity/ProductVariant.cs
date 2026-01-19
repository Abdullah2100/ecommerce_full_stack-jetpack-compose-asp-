namespace api.domain.entity;

public class ProductVariant:GeneralSharedInfoWithId
{
    public ushort Percentage { get; set; }
    public Guid VariantId { get; set; }
    public string Name { get; set; }
    public Guid ProductId { get; set; }
    public Variant? Variant { get; set; } = null;
    public Product? Product { get; set; } = null;
    public ICollection<OrderProductsVariant>? OrderProductsVariants { get; set; } = null;

}