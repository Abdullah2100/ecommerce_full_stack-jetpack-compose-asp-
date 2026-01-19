using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class GeneralSetting:GeneralShredInfo
{
    public string Name { get; set; }
    public decimal Value { get; set; }
}