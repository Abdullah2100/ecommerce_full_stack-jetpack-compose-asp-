using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class CategoryMapperExtension
{
    public static CategoryDto ToDto(this Category category, string url)
    {
        return new CategoryDto
        {
            Id = category.Id,
            Image = string.IsNullOrEmpty(category.Image) ? "" : url + category.Image,
            Name = category.Name
        };
    }

    public static bool IsEmpty(this UpdateCategoryDto category)
    {
        return string.IsNullOrWhiteSpace(category.Name) &&
               category.Image == null;
    }
}