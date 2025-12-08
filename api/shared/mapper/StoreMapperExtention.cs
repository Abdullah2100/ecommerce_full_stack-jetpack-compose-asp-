using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class StoreMapperExtention
{
    public static StoreDto ToDto(this Store store, string url)
    {
        return new StoreDto
        {
            Id = store.Id,
            IsBlocked = store.IsBlock,
            Longitude = store.Addresses?.FirstOrDefault()?.Longitude,
            Latitude = store.Addresses?.FirstOrDefault()?.Latitude,
            Name = store.Name,
            SmallImage = string.IsNullOrEmpty(store.SmallImage) ? "" : url + store.SmallImage,
            WallpaperImage = string.IsNullOrEmpty(store.WallpaperImage) ? "" : url + store.WallpaperImage,
            UpdatedAtAt = store.UpdatedAt,
            UserId = store.UserId,
            UserName = store.user.Name,
        };
    }
    
    public static bool IsEmpty(this UpdateStoreDto dto)
    {
        return string.IsNullOrWhiteSpace(dto.Name) &&
               dto.WallpaperImage == null &&
               dto.SmallImage == null &&
               dto.Longitude == null &&
               dto.Latitude == null;
    }
}