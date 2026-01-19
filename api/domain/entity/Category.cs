using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class Category:GeneralShredInfo
{

    public string Name { get; set; } 
    public Guid  OwnerId { get; set; }
    public bool IsBlocked { get; set; }=false;
    public string Image { get; set; }
    public User User { get; set; }
    public ICollection<SubCategory>? SubCategories {get;set;}
}