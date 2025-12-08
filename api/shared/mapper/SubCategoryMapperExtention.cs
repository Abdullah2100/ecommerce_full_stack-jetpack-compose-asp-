using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;


public static class SubCategoryMapperExtensions
{
    public static SubCategoryDto ToDto(this SubCategory subCategory)
    {
        if (subCategory == null)
            throw new ArgumentNullException(nameof(subCategory));

        return new SubCategoryDto 
        {
            Id = subCategory.Id,
            Name = subCategory.Name,
            CategoryId = subCategory.CategoryId,
            StoreId = subCategory.StoreId
        };
    }

    public static bool IsEmpty(this UpdateSubCategoryDto dto)
    {
        if (dto == null)
            throw new ArgumentNullException(nameof(dto));

        return string.IsNullOrWhiteSpace(dto.Name) &&
               dto.CategoryId == null;
    }
}