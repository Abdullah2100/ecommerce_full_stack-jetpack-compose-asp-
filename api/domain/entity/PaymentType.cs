namespace api.domain.entity;

public class PaymentType:GeneralShredInfo
{
   public string Name { get; set; }
   public bool IsHashCheckOperation { get; set; }
   public string Thumbnail { get; set; }
   public Guid UserId { get; set; }
   public User? User { get; set; } = null;
}