using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class AddressMapperExtension
{
   
   public static AddressDto ToDto(this Address address)
    {
        return new AddressDto
        {
            Id = address.Id,
            Latitude = address.Latitude,
            Longitude = address.Longitude,
            Title = address.Title,
            IsCurrent = address.IsCurrent
        };
    } 
    
    public static DeliveryAddressDto ToDeliveryDto(this Address address)
    {
        return new DeliveryAddressDto 
        {
            Latitude = address.Latitude,
            Longitude = address.Longitude,
        };
    }

    public static bool IsEmpty(this UpdateAddressDto dto)
    {
        return string.IsNullOrWhiteSpace(dto.Title)
               && dto.Longitude == null
               && dto.Latitude == null;
    }
}