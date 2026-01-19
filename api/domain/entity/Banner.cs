using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Banner:GeneralSharedInfoWithCreatedAt
{
    public String Image { get; set; }
    
    [Column(TypeName = "Timestamp")]
    public DateTime  EndAt{ get; set; }
    
    public Guid StoreId { get; set; }
    public Store Store { get; set; }
}