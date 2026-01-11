using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;
using api.util;
using ecommerc_dotnet.midleware.ConfigImplment;

namespace api.application.Services;

public class UserService(
    IConfig config,
    IFileServices fileServices,
    IUnitOfWork unitOfWork,
    IAuthenticationService authenticationService
)
    : IUserServices
{
    public async Task<Result<AuthDto?>> Signup(SignupDto signupDto)
    {
       
        string? validationResult = ClsValidation
            .ValidateInput(
                signupDto.Email,
                signupDto.Password,
                signupDto.Phone
            );

        if (validationResult != null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: validationResult,
                isSuccessful: false,
                statusCode: 400
            );
        }

        bool isExistByEmail =await unitOfWork.UserRepository.IsExistByEmail(signupDto.Email);
        if (isExistByEmail)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "email already exist",
                isSuccessful: false,
                statusCode: 400
            );
        }

        bool isExistByPhone = (await unitOfWork.UserRepository.IsExistByPhone(signupDto.Phone));
        if (isExistByPhone)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "phone already exist",
                isSuccessful: false,
                statusCode: 400
            );
        }

        if (signupDto.Role == 0 && await unitOfWork.UserRepository.IsExist(0))
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "you cannot create a user with exist role",
                isSuccessful: false,
                statusCode: 400
            );
        }

        Guid userId = ClsUtil.GenerateGuid();
        User user = new User
        {
            Id = userId,
            Name = signupDto.Name,
            Phone = signupDto.Phone,
            Password = ClsUtil.HashingText(signupDto.Password),
            Role =(int) (signupDto.Role?? enRole.User),
            DeviceToken = signupDto.DeviceToken ?? "",
            Thumbnail = "",
            CreatedAt = DateTime.Now,
            Email = signupDto.Email,
            UpdatedAt = null,
        };

        unitOfWork.UserRepository.Add(user);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "there are error in create new user",
                isSuccessful: false,
                statusCode: 400
            );
        }

        string token = "", refreshToken = "";

        token = authenticationService.GenerateToken(
            id: userId,
            email: signupDto.Email);

        refreshToken = authenticationService.GenerateToken(
            id: userId,
            email: signupDto.Email,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSuccessful: true,
            data: new AuthDto { RefreshToken = refreshToken, Token = token },
            message: "",
            statusCode: 201
        );
    }

    public async Task<Result<AuthDto?>> Login(LoginDto loginDto)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(loginDto.Username,
                ClsUtil.HashingText(loginDto.Password)
            );

        var isValide = user.IsValidateFunc(false);
        if (isValide is not null)
        {
            return new Result<AuthDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        user.DeviceToken = loginDto.DeviceToken;
        unitOfWork.UserRepository.Update(user);

        int result = await unitOfWork.SaveChanges();
        if (result == 0)
            new Result<AuthDto?>(
                isSuccessful: true,
                data: null,
                message: "",
                statusCode: 200
            ); 


        string token = "", refreshToken = "";

        token = authenticationService.GenerateToken(
            id: user.Id,
            email: user.Email);

        refreshToken = authenticationService.GenerateToken(
            id: user.Id,
            email: user.Email,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSuccessful: true,
            data: new AuthDto { RefreshToken = refreshToken, Token = token },
            message: "",
            statusCode: 200
        );
    }


    public async Task<Result<UserInfoDto?>> GetMe(Guid id)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(id);

        var validate = user.IsValidateFunc(false);
        if (validate is not null)
        {
            return new Result<UserInfoDto?>(
                isSuccessful: false,
                data: null,
                message: validate.Message,
                statusCode: validate.StatusCode
            );
        }

        return new Result<UserInfoDto?>(
            isSuccessful: true,
            data: user!.ToUserInfoDto(config.getKey("url_file")),
            message: "",
            statusCode: 200
        );
    }


    public async Task<Result<List<UserInfoDto>?>> GetUsers(
        int page,
        Guid id)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(id);

        var isValide = user.IsValidateFunc();
        if (isValide is not null)
        {
            return new Result<List<UserInfoDto>?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        List<UserInfoDto> users = (await unitOfWork.UserRepository
                .GetUsers(page, 25))
            .Select(u => u.ToUserInfoDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<UserInfoDto>?>
        (
            data: users,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<int?>> GetUsersPages(Guid id,int pageLenght)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(id);

        var isValide = user.IsValidateFunc();
        if (isValide is not null)
        {
            return new Result<int?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        var userPages = await unitOfWork.UserRepository.GetUserCount();
        var pageUserCount = userPages>0?(int)Math.Ceiling((double)userPages/pageLenght):0;
        return new Result<int?>(
            isSuccessful: true,
            data: pageUserCount,
            message:  "",
            statusCode: 200
        ); 
    }

    public async Task<Result<bool>> BlockOrUnBlockUser(Guid id, Guid userId)
    {
        User? admin = await unitOfWork.UserRepository
            .GetUser(id);

        var validateFunc = admin.IsValidateFunc();
        if (validateFunc is not null)
        {
            return new Result<bool>(
                isSuccessful: false,
                data: false,
                message: validateFunc.Message,
                statusCode: validateFunc.StatusCode
            );
        }

        User? user = await unitOfWork.UserRepository.GetUser(userId);

        validateFunc = user.IsValidateFunc();

        //this to handle if user that admin want to block is not admin
        if (validateFunc is not null)
        {
            return new Result<bool>(
                isSuccessful: false,
                data: false,
                message: $"unable to {(user?.IsBlocked == true ? "block" : "unblock")}  user",
                statusCode: validateFunc?.StatusCode ?? 400
            );
        }

        user!.IsBlocked = !user.IsBlocked;

        if (user is { IsBlocked: true, Role: 0 })
        {
            return new Result<bool>(
                isSuccessful: false,
                data: false,
                message: "you could not block admin user ",
                statusCode: 400
            ); 
        }

        unitOfWork.UserRepository.Update(user);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while change user Blocking status",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: false,
            statusCode: 204
        );
    }


    public async Task<Result<UserInfoDto?>> UpdateUser(
        UpdateUserInfoDto userDto,
        Guid id,
       bool isUpdateWillBeTop=false)
    {
        if (userDto.IsEmpty())
            return new Result<UserInfoDto?>
            (
                data: null,
                message: "no data changes",
                isSuccessful: false,
                statusCode: 200
            );


        User? user = await unitOfWork.UserRepository.GetUser(id);

        var isValide = user.IsValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<UserInfoDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }


        if (userDto.Phone is not null && user?.Phone != userDto.Phone)
        {
            bool isExistPhone = await unitOfWork.UserRepository.IsExistByPhone(userDto.Phone ?? "");

            if (isExistPhone)
            {
                return new Result<UserInfoDto?>
                (
                    data: null,
                    message: "phone already exist",
                    isSuccessful: false,
                    statusCode: 400
                );
            }
        }

        string? hashedPassword =
            string.IsNullOrEmpty(userDto.Password)
            || string.IsNullOrEmpty(userDto.NewPassword)
                ? null
                : ClsUtil.HashingText(userDto.NewPassword);

        if (userDto.Password != null && userDto.NewPassword != null)
        {
            if (user?.Password != ClsUtil.HashingText(userDto.Password))
            {
                return new Result<UserInfoDto?>
                (
                    data: null,
                    message: "password not corrected",
                    isSuccessful: false,
                    statusCode: 400
                );
            }
        }

        string? profile = null;
        if (userDto.Thumbnail != null)
        {
            profile = await fileServices.SaveFile(userDto.Thumbnail, EnImageType.Profile);
        }

        user.Thumbnail = profile ?? user.Thumbnail;
        user.Name = userDto.Name ?? user.Name;
        user.Phone = userDto.Phone ?? user.Phone;
        user.UpdatedAt = DateTime.Now;
        user.Password = hashedPassword ?? user.Password;
        
         unitOfWork.UserRepository.Update(user);

        if (isUpdateWillBeTop)
        {
            return new Result<UserInfoDto?>
            (
                data: null,
                message: "",
                isSuccessful: true,
                statusCode: 200
            );  
        }
        
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<UserInfoDto?>
            (
                data: null,
                message: "error while updating user",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<UserInfoDto?>
        (
            data: user.ToUserInfoDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<AddressDto?>> AddAddressToUser(
        CreateAddressDto addressDto,
        Guid id
    )
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(id);
        var isValide = user.IsValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<AddressDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        int addressCount = await unitOfWork.AddressRepository.GetAddressCount(id);

        if (addressCount == 20)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "maximum 20 addresses reached",
                isSuccessful: false,
                statusCode: 400
            );
        }

        Address address = new Address
        {
            Id = ClsUtil.GenerateGuid(),
            Longitude = addressDto.Longitude,
            Latitude = addressDto.Latitude,
            Title = addressDto.Title,
            OwnerId = user!.Id,
            IsCurrent = true
        };

        unitOfWork.AddressRepository.MakeAddressNotCurrentToId(user.Id);

        unitOfWork.AddressRepository.Add(address);
        var result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "error while adding address",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<AddressDto?>
        (
            data: address.ToDto(),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }


    public async Task<Result<AddressDto?>> UpdateUserAddress(
        UpdateAddressDto addressDto,
        Guid id)
    {
        if (addressDto.IsEmpty())
            return new Result<AddressDto?>
            (
                data: null,
                message: "nothing to be updated",
                isSuccessful: true,
                statusCode: 200
            );

        User? user = await unitOfWork.UserRepository
            .GetUser(id);
        var isValide = user.IsValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<AddressDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        if (
            (addressDto.Longitude is null && addressDto.Latitude is not null) ||
            (addressDto.Longitude is not null && addressDto.Latitude is null)
        )
        {
            return new Result<AddressDto?>(
                isSuccessful: false,
                data: null,
                message: "when update address you must change both longitude and latitude not one of them only ",
                statusCode: 400
            );
        }


        Address? address = await unitOfWork.AddressRepository.GetAddress(addressDto.Id);

        if (address is null)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "address not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (address.OwnerId != id)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "address not owned",
                isSuccessful: false,
                statusCode: 400
            );
        }


        address.Longitude = addressDto.Longitude ?? address.Longitude;
        address.Title = addressDto.Title ?? address.Title;
        address.Latitude = addressDto.Latitude ?? address.Latitude;

        unitOfWork.AddressRepository.Update(address);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<AddressDto?>
            (
                data: null,
                message: "error while updating address",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<AddressDto?>
        (
            data: address.ToDto(),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<bool>> DeleteUserAddress(Guid addressId, Guid id)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(id);
        var isValide = user.IsValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSuccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        Address? address = await unitOfWork.AddressRepository.GetAddress(addressId);

        if (address is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "address not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (address.OwnerId != id)
        {
            return new Result<bool>
            (
                data: false,
                message: "address not owned",
                isSuccessful: false,
                statusCode: 400
            );
        }

        if (address.IsCurrent)
        {
            return new Result<bool>
            (
                data: false,
                message: "could not delete current address",
                isSuccessful: false,
                statusCode: 400
            );
        }

        unitOfWork.AddressRepository.Delete(addressId);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete address",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }


    public async Task<Result<bool>> UpdateUserCurrentAddress(Guid addressId, Guid id)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(id);
        var isValide = user.IsValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSuccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        Address? address = await unitOfWork.AddressRepository.GetAddress(addressId);

        if (address is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "address not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (address.OwnerId != id)
        {
            return new Result<bool>
            (
                data: false,
                message: "address not owned",
                isSuccessful: false,
                statusCode: 400
            );
        }

        if (address.IsCurrent)
        {
            return new Result<bool>
            (
                data: false,
                message: "address is already current address",
                isSuccessful: false,
                statusCode: 400
            );
        }

        unitOfWork.AddressRepository.MakeAddressNotCurrentToId(user!.Id);


        unitOfWork.AddressRepository.UpdateCurrentLocation(addressId, user!.Id);
        var result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while update current address",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }


    public async Task<Result<bool>> GenerateOtp(ForgetPasswordDto forgetPasswordDto)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(forgetPasswordDto.Email);

        var isValide = user.IsValidateFunc(false);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSuccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        string otp = ClsUtil.GenerateGuid().ToString().Substring(0, 6).Replace("-", "");
        bool isOtpExist = await unitOfWork.PasswordRepository.IsExist(otp, user!.Email);
        bool isExist = isOtpExist;

        if (isExist)
        {
            do
            {
                otp = ClsUtil.GenerateGuid().ToString().Substring(0, 6).Replace("-", "");
                isOtpExist = await unitOfWork.PasswordRepository.IsExist(otp, user!.Email);
            } while (isOtpExist);
        }

        unitOfWork.PasswordRepository.Add(
            new ReseatPasswordOtp
            {
                Email = forgetPasswordDto.Email,
                CreatedAt = DateTime.Now.AddHours(1),
                Id = ClsUtil.GenerateGuid(),
                Otp = otp
            }
        );
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while generate otp",
                isSuccessful: false,
                statusCode: 400
            );
        }

        var SendMessageSerivce = new SendMessageServices(new EmailServices(config));
        bool emailSendResult = await SendMessageSerivce.SendMessage(message: otp, otp);

        if (!emailSendResult)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while send  otp email",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: false,
            statusCode: 204
        );
    }

    public async Task<Result<bool>> OtpVerification(CreateVerificationDto otp)
    {
        bool isExistUser = await unitOfWork.UserRepository
            .IsExistByEmail(otp.Email);
        if (!isExistUser)
        {
            return new Result<bool>
            (
                data: false,
                message: "user not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        ReseatPasswordOtp? otpResult = await unitOfWork.PasswordRepository.GetOtp(otp.Otp, otp.Email);


        if (otpResult is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "otp not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        otpResult.IsValidated = true;

        unitOfWork.PasswordRepository.Update(otpResult);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while update otp",
                isSuccessful: false,
                statusCode: 400
            );
        }


        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<AuthDto?>> ReseatePassword(CreateReseatePasswordDto otp)
    {
        bool isExistUser = await unitOfWork.UserRepository
            .IsExistByEmail(otp.Email);
        if (!isExistUser)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "user not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        ReseatPasswordOtp? otpResult = await unitOfWork.PasswordRepository.GetOtp(otp.Otp, otp.Email, true);


        if (otpResult is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "otp not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        User? user = await unitOfWork.UserRepository.GetUser(otp.Email);

        var isValide = user.IsValidateFunc();
        if (isValide is not null)
        {
            return new Result<AuthDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        user.Password = ClsUtil.HashingText(otp.Password);

        unitOfWork.UserRepository.Update(user);
        int result = await unitOfWork.SaveChanges();
        
        if (result == 0)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "error while update user password",
                isSuccessful: false,
                statusCode: 400
            );
        }


        string token = "", refreshToken = "";

        token = authenticationService.GenerateToken(
            id: user.Id,
            email: user.Email
            );

        refreshToken = authenticationService.GenerateToken(
            id: user.Id,
            email: user.Email,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSuccessful: true,
            data: new AuthDto { RefreshToken = refreshToken, Token = token },
            message: "",
            statusCode: 200
        );
    }
}