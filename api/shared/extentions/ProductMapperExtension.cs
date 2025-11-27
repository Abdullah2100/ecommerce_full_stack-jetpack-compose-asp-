using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.extentions;

public static class ProductMapperExtension
{
   
    public static ProductDto ToDto(this Product product,string url)
    {
        return new ProductDto
        {
            Id = product.Id,
            Name = product.Name,
            Description = product.Description,
            Price = product.Price,
            Thumbnail = string.IsNullOrEmpty(product.Thumbnail) ? "" : url+ product.Thumbnail,
            CategoryId = product.SubCategory.CategoryId,
            ProductImages =product.ProductImages.Select(pi=>url+pi.Path).ToList(),
            ProductVariants = product.ProductVariants
                .GroupBy(pv => pv.VariantId, (key, g)
                    => g.Select(pvH=>pvH.ToProductVarientDto()).ToList()
                ).ToList(),
            StoreId = product.StoreId,
            SubcategoryId = product.SubcategoryId,
        };
    }
 
    public static AdminProductsDto ToAdminDto(this Product product,string url)
    {
        return new AdminProductsDto 
        {
            Id = product.Id,
            Name = product.Name,
            Description = product.Description,
            Price = product.Price,
            Thumbnail = string.IsNullOrEmpty(product.Thumbnail) ? "" : url+ product.Thumbnail,
            StoreName = product.Store.Name,
            ProductImages =product?.ProductImages==null?new List<string>():product.ProductImages.Select(pi=>url+pi.Path).ToList(),
            ProductVariants = product?.ProductVariants==null?new List<List<AdminProductVarientDto>>(): product.ProductVariants
                .GroupBy(pv => pv.VariantId, (key, g)
                    => g.Select(pvH=>pvH.ToAdminProductVarientDto()).ToList()
                ).ToList(),
            Subcategory =  product.SubCategory.Name,
        };
    }

    public static bool IsEmpty(this UpdateProductDto dto)
    {
        return string.IsNullOrEmpty(dto.Name) &&
               string.IsNullOrEmpty(dto.Description)
               && dto.Thumbnail == null
               && dto.SubcategoryId == null
               && dto.Price == null
               && dto.ProductVariants == null
               && dto.Images == null
               && dto.Deletedimages == null
               && dto.DeletedProductVariants==null
            ;    
    }


    
}