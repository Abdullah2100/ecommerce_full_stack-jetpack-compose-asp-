using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class OrderProductMapperExtension
{
    public static OrderProductDto ToOrderProductDto(this OrderItem orderItem,string url)
    {
        return new OrderProductDto
        {
            Id = orderItem.ProductId,
            Name = orderItem.Product.Name,
            Thumbnail=url+orderItem.Product.Thumbnail

        };
    }
}