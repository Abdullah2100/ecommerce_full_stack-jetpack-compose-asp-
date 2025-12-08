using api.application.Result;
using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class UserMapperExtension
{
    public static UserInfoDto ToUserInfoDto(this User user, string url)
    {
        return new UserInfoDto
        {
            Id = user.Id,
            Email = user.Email,
            Name = user.Name,
            Phone = user.Phone,
            Thumbnail = string.IsNullOrEmpty(user.Thumbnail) ? "" : url + user.Thumbnail,
            IsActive = user.IsBlocked == false,
            IsAdmin = user.Role == 0,
            Address = user.Addresses?.Select(ad => ad.ToDto()).ToList(),
            StoreId = user?.Store?.Id,
            StoreName = user?.Store?.Name ?? "",
        };
    }


    public static UserDeliveryInfoDto ToDeliveryInfoDto(this User user, string url)
    {
        return new UserDeliveryInfoDto
        {
            Email = user.Email,
            Name = user.Name,
            Phone = user.Phone,
            Thumbnail = string.IsNullOrEmpty(user.Thumbnail) ? "" : url + user.Thumbnail,
        };
    }

    public static bool IsEmpty(this UpdateUserInfoDto dto)
    {
        return string.IsNullOrWhiteSpace(dto.Name) &&
               string.IsNullOrWhiteSpace(dto.Phone) &&
               dto.Thumbnail == null &&
               string.IsNullOrWhiteSpace(dto.Password) &&
               string.IsNullOrWhiteSpace(dto.NewPassword)
            ;
    }

    public static Result<AuthDto?>? IsHasStore(User user)
    {
        switch (user.Store is not null)
        {
            case true:
            {
                if (user.Store.IsBlock)
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "store is Blocked",
                        isSuccessful: false,
                        statusCode: 400
                    );
                }

                return null;
            }

            default:
            {
                if (user.Store is null)
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "you must has store before done this operation",
                        isSuccessful: false,
                        statusCode: 400
                    );
                }

                return null;
            }
        }
    }

    public static Result<AuthDto?>? IsValidateFunc(
        this User? user,
        bool? isAdmin = true,
        bool isStore = false)
    {
        if (user is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "user not found",
                isSuccessful: false,
                statusCode: 404
            );
        }


        //validate user if it is admin or user according to isAdmin feild 
        switch (isAdmin)
        {
            case null: return isStore ? IsHasStore(user) : null;

            case false:
            {
                if (user.IsBlocked)
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "user is blocked",
                        isSuccessful: false,
                        statusCode: 400
                    );
                }

                //check if user has store
                return isStore ? IsHasStore(user) : null;
            }
            default:
            {
                if (user is { Role: 1, IsBlocked: true })
                {
                    return new Result<AuthDto?>
                    (
                        data: null,
                        message: "user not havs the permission",
                        isSuccessful: false,
                        statusCode: 400
                    );
                }

                //check if admin has store
                return isStore ? IsHasStore(user) : null;
            }
        }
    }

    public static bool IsUpdateAnyFeild(this UpdateUserInfoDto dto)
    {
        return dto.Thumbnail != null ||
               !(string.IsNullOrEmpty(dto.NewPassword) && string.IsNullOrEmpty(dto.Password)) ||
               !string.IsNullOrEmpty(dto.Phone) ||
               !string.IsNullOrEmpty(dto.Name);
    }
}