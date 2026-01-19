using System.ComponentModel.DataAnnotations.Schema;

namespace api.domain.entity;

public class ReseatPasswordOtp:GeneralSharedInfoWithCreatedAt
{
    public string Email { get; set; }
    public string Otp { get; set; }
    public bool IsValidated { get; set; } = false;
}