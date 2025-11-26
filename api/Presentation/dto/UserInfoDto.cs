using System.ComponentModel.DataAnnotations;

namespace api.Presentation.dto
{
    public class UserInfoDto
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public bool IsAdmin { get; set; } = false;
        public string Phone { get; set; }
        public string Email { get; set; }
        public string StoreName { get; set; } = "";
        public bool IsActive { get; set; } = true;
        public string Thumbnail { get; set; }
        public List<AddressDto>? Address { get; set; }
        public Guid? StoreId { get; set; }
    }

    


    public class UserDeliveryInfoDto
    {
        public required string Name { get; set; } 
        public string Phone { get; set; }
        public string Email { get; set; }
        public string Thumbnail { get; set; }
    }

    public class UpdateUserInfoDto
    {
        [StringLength(maximumLength: 50 , ErrorMessage = "Enter Valide Name")]
        public string? Name { get; set; } = null;
        
        [StringLength(maximumLength: 13, ErrorMessage = "Enter Valide Name")]
        public string? Phone { get; set; } = null;
        public IFormFile? Thumbnail { get; set; } = null;
        public string? Password { get; set; } = null;
        public string? NewPassword { get; set; } = null;
       
    }
}