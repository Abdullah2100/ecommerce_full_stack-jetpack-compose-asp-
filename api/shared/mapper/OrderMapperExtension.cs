using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class OrderMapperExtension
{
    public static OrderDto ToDto(this Order order,string url)
    {
        return new OrderDto
        {
            Id = order.Id,
            DeliveryFee = order.DistanceFee,
            Name = order.User.Name,
            Longitude = order.Longitude,
            Latitude = order.Latitude,
            Status = order.Status.ToOrderStatusName(),
            TotalPrice = order.TotalPrice,
            Symbol = order.Symbol,
            UserPhone = order.User.Phone,
            OrderItems = order
                .Items
                .Select(it=>it.ToOrderItemDto(url))
                .ToList()
        };
    }
    
    public static DeliveryOrderDto ToDeliveryDto(this Order order,string url)
    {
        return new DeliveryOrderDto 
        {
            Id = order.Id,
            DeliveryFee = order.DistanceFee,
            Name = order.User.Name,
            Longitude = order.Longitude,
            Latitude = order.Latitude,
            Status = order.Status,
            TotalPrice = order.TotalPrice,
            UserPhone = order.User.Phone,
            OrderItems = order
                .Items
                .Select(it=>it.ToDeliveryOrderItemDto(url))
                .ToList()
        };
    }
}