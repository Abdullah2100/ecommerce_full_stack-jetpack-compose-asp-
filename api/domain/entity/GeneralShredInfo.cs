using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class GeneralShredInfo : GeneralSharedInfoWithCreatedAt
{
    [Column(TypeName = "Timestamp")] public DateTime? UpdatedAt { get; set; } = null;
}

public class GeneralSharedInfoWithCreatedAt : GeneralSharedInfoWithId
{
    [Column(TypeName = "Timestamp")] public DateTime CreatedAt { get; set; } = DateTime.Now;
}

public class GeneralSharedInfoWithId
{
    [Key] public Guid Id { get; set; }
}