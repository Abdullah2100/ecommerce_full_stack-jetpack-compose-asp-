using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Currency
{
    [Key]
    public Guid Id { get; set; }
    public string Name { get; set; }
    public int Value { get; set; }
    public string Symbol { get; set; }

    public bool IsDefault { get; set; } = false;
    
    [Column(TypeName = "Timestamp")]
    public DateTime CreatedAt { get; set; }

    [Column(TypeName = "Timestamp")] 
    public DateTime? UpdatedAt { get; set; } = null; 
}