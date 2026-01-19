using api.domain.entity;

namespace api.Presentation.dto;

public class PaymentTypeDto
{
   public Guid Id { get; set; }
   public string Name { get; set; }
   public bool IsHashCheckOperation { get; set; }
   public string Thumbnail { get; set; }
}

public class CreatePaymentTypeDto
{
   public string Name { get; set; }
   public bool IsHashCheckOperation { get; set; }
   public IFormFile Thumbnail { get; set; }
}

public class UpdatePaymentTypeDto
{
   public Guid Id { get; set; }
   public string? Name { get; set; } = null;
   public bool? IsHashCheckOperation { get; set; } = null;
   public IFormFile? Thumbnail { get; set; } = null;
}