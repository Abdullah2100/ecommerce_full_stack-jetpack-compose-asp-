using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class OrderExperientialExtension
{
    public static OrderVariantDto ToOrderVarientDto(this OrderProductsVariant orderProductsVariant)
    {
        return new OrderVariantDto
        {
            Name = orderProductsVariant.ProductVariant.Product.Name,
            VarientName = orderProductsVariant.ProductVariant.Variant.Name,

        };
    }
    
}