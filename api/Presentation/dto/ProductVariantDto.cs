using System.ComponentModel.DataAnnotations;

namespace api.Presentation.dto
{
    public class ProductVariantDto
    {
        public Guid Id { get; set; }
        public Guid ProductId { get; set; }
        public string? Name { get; set; }
        public decimal Percentage { get; set; }
        public Guid VariantId { get; set; }
    }
    
     public class CreateProductVariantDto
    {
        [StringLength(maximumLength:50,MinimumLength =3 ,ErrorMessage= "name must not be empty")]
        public string Name { get; set; }
        public decimal Percentage { get; set; } = 1;
        public Guid VariantId { get; set; }
    }
    
    public class AdminProductVariantDto
    {
        [StringLength(maximumLength:50 ,ErrorMessage= "name must not  be empty")]
        public string? Name { get; set; }
        public decimal Percentage { get; set; }
        public string?  VariantName { get; set; } 
    }
}