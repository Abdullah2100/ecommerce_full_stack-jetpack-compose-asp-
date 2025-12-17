using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface IUserServices
{
    public Task<Result<AuthDto?>> Signup(SignupDto signupDto);
    public Task<Result<AuthDto?>> Login(LoginDto loginDto);


    public Task<Result<UserInfoDto?>> GetMe(Guid id);

    public Task<Result<List<UserInfoDto>?>> GetUsers(int page, Guid id);
    public Task<Result<int?>> GetUsersPages(Guid id,int pageLenght=25);

    public Task<Result<bool>> BlockOrUnBlockUser(Guid id,Guid userId);
    
    public Task<Result<UserInfoDto?>> UpdateUser(
        UpdateUserInfoDto userDto, Guid id
        ,bool isUpdateWillBeUp=false);

    public Task<Result<AddressDto?>> AddAddressToUser(CreateAddressDto addressDto, Guid id);
    public Task<Result<AddressDto?>> UpdateUserAddress(UpdateAddressDto addressDto, Guid id);
    public Task<Result<bool>> DeleteUserAddress(Guid addressId, Guid id);
    public Task<Result<bool>> UpdateUserCurrentAddress(Guid addressId, Guid id);

    public Task<Result<bool>> GenerateOtp(ForgetPasswordDto forgetPasswordDto);
    public Task<Result<bool>> OtpVerification(CreateVerificationDto createVerificationDto);
    public Task<Result<AuthDto?>> ReseatePassword(CreateReseatePasswordDto createReseatePasswordDto);
}