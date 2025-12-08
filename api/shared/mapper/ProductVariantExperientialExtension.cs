using api.domain.entity;
using api.Presentation.dto;

public static class ProductVariantExperientialExtension
{
    public static ProductVariantDto ToProductVariantDto(this ProductVariant productVariant)
    {
        return new ProductVariantDto
        {
            ProductId = productVariant.ProductId,
            Name = productVariant.Name,
            Percentage = productVariant.Percentage,
            VariantId = productVariant.Id
        };
    }
    
    public static AdminProductVariantDto ToAdminProductVariantDto(this ProductVariant productVariant)
    {
        return new AdminProductVariantDto()
        {
            Name = productVariant.Name,
            Percentage = productVariant.Percentage,
            VariantName = productVariant?.Variant?.Name??""
            
        };
    }
    
}