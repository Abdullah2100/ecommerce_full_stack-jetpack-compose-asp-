using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class BannerMapperExtension
{
    public static BannerDto ToDto(this Banner banner,string url)
    {
        return new BannerDto
        {
            CreatedAt = banner.CreatedAt,
            EndAt = banner.EndAt,
            Id = banner.Id,
            Image = string.IsNullOrEmpty(banner.Image) ? "" :url+ banner.Image,
            StoreId = banner.StoreId,
        };
    }

    
    
}