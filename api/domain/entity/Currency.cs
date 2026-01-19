using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Currency:GeneralShredInfo
{
    
    public string Name { get; set; }
    public int Value { get; set; }
    
    [Column(TypeName = "varchar(10)")]
    public string Symbol { get; set; }

    public bool IsDefault { get; set; } = false;
    
 
}