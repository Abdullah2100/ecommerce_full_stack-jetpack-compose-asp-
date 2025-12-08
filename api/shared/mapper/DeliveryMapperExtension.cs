using api.application.Result;
using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class DeliveryMapperExtension
{
    public static DeliveryDto ToDto(this Delivery delivery, string url)
    {
        return new DeliveryDto
        {
            Id = delivery.Id,
            UserId = delivery.UserId,
            UpdatedAt = delivery.UpdatedAt,
            Address = delivery?.Address?.ToDeliveryDto(),
            Analyse = null,
            Thumbnail = string.IsNullOrEmpty(delivery?.Thumbnail) ? null : url + delivery.Thumbnail,
            User = delivery?.User.ToDeliveryInfoDto(url),
            IsAvailable = delivery?.IsAvailable ?? false
        };
    }

    public static Result<DeliveryDto>? IsValidated(this Delivery? delivery)
    {
        if (delivery is null)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: "delivery not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (delivery.IsBlocked)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: "delivery is blocked",
                isSuccessful: false,
                statusCode: 404
            );
        }

        return null;
    }
}