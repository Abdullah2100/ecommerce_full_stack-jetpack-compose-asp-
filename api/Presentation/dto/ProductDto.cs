using System.ComponentModel.DataAnnotations;

namespace api.Presentation.dto
{
    
    
    public class ProductDto
    {
        public Guid Id { get; set; }
        public String Symbol { get; set; } 
        public string Name { get; set; }
        public string Description { get; set; }
        public string  Thumbnail { get; set; }
        public Guid SubcategoryId { get; set; }
        public Guid CategoryId { get; set; }
        public Guid StoreId { get; set; }
        public decimal Price { get; set; }
        public List<List<ProductVariantDto>>? ProductVariants { get; set; }
        public List<string> ProductImages { get; set; }
    }
    
    public class CreateProductDto
    {
        [StringLength(maximumLength: 100, MinimumLength = 5, ErrorMessage = "name must not be empty")]
        public string Name { get; set; }
        public string Description { get; set; }
        public IFormFile Thumbnail { get; set; }
        public Guid SubcategoryId { get; set; }
        public decimal Price { get; set; }
        public String Symbol { get; set; } 

        public List<CreateProductVariantDto>? ProductVariants { get; set; } = null;
        public List<IFormFile> Images { get; set; }
    }
    
    
    public class UpdateProductDto 
    {
        [Required]public Guid Id { get; set; }
        
        [StringLength(maximumLength: 100 , ErrorMessage = "name must not be empty")]
        public string? Name { get; set; }= null;
        
        public string? Description { get; set; }= null;
        public IFormFile?  Thumbnail { get; set; }= null;
        public Guid? SubcategoryId { get; set; }= null;
        [Required] public Guid StoreId { get; set; }
        public decimal? Price { get; set; }= null;
        public String? Symbol { get; set; } = null; 

        public List<CreateProductVariantDto>? ProductVariants { get; set; } = null;
        public List<CreateProductVariantDto>? DeletedProductVariants { get; set; } = null;
        public List<IFormFile>? Images { get; set; }= null;
        public List<string>? Deletedimages { get; set; }= null;

     
    }
    
    
    public class AdminProductsDto
    {
        public Guid Id { get; set; }
        
        [StringLength(maximumLength: 100 , ErrorMessage = "name must not be empty")]
        public string Name { get; set; }
        
        public string Description { get; set; }
        public string  Thumbnail { get; set; }
        public string Subcategory { get; set; }
        public string StoreName { get; set; }
        public decimal Price { get; set; }
        public String Symbol { get; set; } 

        public List<List<AdminProductVariantDto>>? ProductVariants { get; set; }
        public List<string> ProductImages { get; set; } 
    }
}