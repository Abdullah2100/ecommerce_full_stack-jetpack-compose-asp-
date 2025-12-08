using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class ExperientialExtension
{
    public static VariantDto ToDto(this Variant variant)
    {
        return new VariantDto
        {
            Id = variant.Id,
            Name = variant.Name,

        };
    }

    public static bool IsEmpty(this UpdateVariantDto dto)
    {
        return string.IsNullOrWhiteSpace(dto.Name?.Trim());

    }
}
    
