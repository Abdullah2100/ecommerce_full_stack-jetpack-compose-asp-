using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;
using api.util;
using ecommerc_dotnet.midleware.ConfigImplment;

namespace api.application.Services;

public enum EnBelongToType
{
    Admin,
    Store
};

public class DeliveryServices(
    IConfig config,
    IWebHostEnvironment host,
    IUnitOfWork unitOfWork,
    IFileServices fileServices,
    IUserServices userServices,
    IAuthenticationService authenticationService
)
    : IDeliveryServices
{
    public async Task<Result<AuthDto?>> Login(LoginDto loginDto)
    {
        if (string.IsNullOrWhiteSpace(loginDto.DeviceToken))
            return new Result<AuthDto?>
            (
                data: null,
                message: "you should login from phone",
                isSuccessful: false,
                statusCode: 400
            );

        User? user = await unitOfWork.UserRepository
            .GetUser(
                loginDto.Username,
                ClsUtil.HashingText(loginDto.Password));


        var isValide = user.IsValidateFunc(isAdmin: false);

        if (isValide is not null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: isValide.Message,
                isSuccessful: false,
                statusCode: isValide.StatusCode
            );
        }

        Delivery? delivery = await unitOfWork.DeliveryRepository.GetDeliveryByUserId(user.Id);

        if (delivery is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "delivery not found",
                isSuccessful: false,
                statusCode: 404
            );
        }


        if (delivery.IsBlocked)
            return new Result<AuthDto?>
            (
                data: null,
                message: "delivery is blocked",
                isSuccessful: false,
                statusCode: 404
            );
        delivery.DeviceToken = loginDto.DeviceToken;

        unitOfWork.DeliveryRepository.Update(delivery);
        int result = await unitOfWork.SaveChanges();
        if (result == 0)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "error while adding delivery",
                isSuccessful: false,
                statusCode: 400
            );
        }


        string? token = null, refreshToken = null;
        token = authenticationService.GenerateToken(
            id: delivery.Id,
            email: delivery.User.Email);

        refreshToken = authenticationService.GenerateToken(
            id: delivery.Id,
            email: delivery.User.Email,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSuccessful: true,
            data: new AuthDto { RefreshToken = refreshToken, Token = token },
            message: "",
            statusCode: 200
        );
    }

    public async Task<Result<DeliveryDto?>> CreateDelivery(
        Guid userId,
        CreateDeliveryDto deliveryDto
    )
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(userId);


        var admin = user.IsValidateFunc(isAdmin: true);
        var store = user.IsValidateFunc(isAdmin: false, isStore: true);


        if (admin is not null && user?.Role == 0 || store != null)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: admin?.Message ?? store?.Message,
                isSuccessful: false,
                statusCode: admin?.StatusCode ?? store!.StatusCode
            );
        }


        if (await unitOfWork.DeliveryRepository.IsExistByUserId(deliveryDto.UserId))
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "delivery already exists",
                isSuccessful: false,
                statusCode: 400
            );
        }


        string? deliveryThumnail = null;
        if (deliveryDto.Thumbnail is not null)
        {
            deliveryThumnail = await fileServices
                .SaveFile(
                    deliveryDto.Thumbnail,
                    EnImageType.Delivery);
        }


        var addressId = ClsUtil.GenerateGuid();
        var id = ClsUtil.GenerateGuid();
        Address address = new Address
        {
            Id = addressId,
            Title = "my Place",
            CreatedAt = DateTime.Now,
            OwnerId = id
        };

        Delivery delivery = new Delivery
        {
            Id = id,
            CreatedAt = DateTime.Now,
            UserId = deliveryDto.UserId,
            Thumbnail = deliveryThumnail,
            Address = address,
            BelongTo = user?.Store?.Id ?? userId
        };

        unitOfWork.DeliveryRepository.Add(delivery);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "error while adding delivery",
                isSuccessful: false,
                statusCode: 400
            );
        }

        delivery = await unitOfWork.DeliveryRepository.GetDelivery(id);

        return new Result<DeliveryDto?>
        (
            data: delivery?.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<DeliveryDto?>> UpdateDeliveryStatus(Guid id, bool status)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository
            .GetDelivery(id);
        if (delivery is null)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "delivery not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (delivery.IsBlocked)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "delivery is blocked",
                isSuccessful: false,
                statusCode: 404
            );
        }

        delivery.IsBlocked = status;

        unitOfWork.DeliveryRepository.Update(delivery);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: "error while update delivery",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<DeliveryDto?>
        (
            data: delivery?.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<DeliveryDto?>> GetDelivery(Guid id)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository
            .GetDelivery(id);

        var isValid = delivery.IsValidated(); 

        if (isValid is not null)
        {
            return new Result<DeliveryDto?>
            (
                data: null,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode 
            );
        }

        var deliveryDto = delivery.ToDto(config.getKey("url_file"));
        deliveryDto.Analyse = await unitOfWork.DeliveryRepository.GetDeliveryAnalys(delivery.Id);

        return new Result<DeliveryDto?>
        (
            data: deliveryDto,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<List<DeliveryDto>>> GetDeliveries(
        Guid belongToId,
        int pageNumber,
        int pageSize
    )
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(belongToId);

        EnBelongToType belongType = EnBelongToType.Admin;

        switch (user.Role == 0)
        {
            case true:
            {
                belongType = EnBelongToType.Admin;
            }
                break;
            default:
            {
                belongType = EnBelongToType.Store;
            }
                break;
        }

        Guid id = Guid.NewGuid();
        switch (belongType)
        {
            case EnBelongToType.Store:
            {
                var isValidated = user.IsValidateFunc(isStore: true);
                if (isValidated is not null)
                {
                    return new Result<List<DeliveryDto>>
                    (
                        data: new List<DeliveryDto>(),
                        message: isValidated.Message,
                        isSuccessful: false,
                        statusCode: isValidated.StatusCode
                    );
                }

                id = user.Store.Id;
            }
                break;
            case EnBelongToType.Admin:
            {
                var isValidated = user.IsValidateFunc();
                if (isValidated is not null)
                {
                    return new Result<List<DeliveryDto>>
                    (
                        data: new List<DeliveryDto>(),
                        message: isValidated.Message,
                        isSuccessful: false,
                        statusCode: isValidated.StatusCode
                    );
                }

                id = user.Id;
            }
                break;
        }


        List<DeliveryDto>? deliveryDto = (await unitOfWork.DeliveryRepository
                .GetDeliveriesByBelongTo(id, pageNumber, pageSize))
            ?.Select((de) => de.ToDto(config.getKey("url_file")))
            ?.ToList();

        if (deliveryDto is not null)
            foreach (var delivery in deliveryDto)
            {
                delivery.Analyse = await unitOfWork.DeliveryRepository.GetDeliveryAnalys(delivery.Id);
            }

        return new Result<List<DeliveryDto>>
        (
            data: deliveryDto,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<DeliveryDto>> UpdateDelivery(UpdateDeliveryDto deliveryDto, Guid id)
    {
        Delivery? delivery = await unitOfWork.DeliveryRepository
            .GetDelivery(id);

        var isValidated = delivery.IsValidated();

        if (isValidated is not null)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: isValidated.Message,
                isSuccessful: false,
                statusCode: isValidated.StatusCode
            );
        }

        bool isPassOperation = false;

        if (deliveryDto.Longitude is not null && deliveryDto.Latitude is not null)
        {
            var addressHolder = delivery.Address ?? new Address()
            {
                Id = ClsUtil.GenerateGuid(),
                CreatedAt = DateTime.Now,
                OwnerId = delivery.Id,
                IsCurrent = true,
                Title = "My Place"
            };
            addressHolder.Longitude = deliveryDto.Longitude;
            addressHolder.Latitude = deliveryDto.Latitude;
            addressHolder.IsCurrent = true;
            if (delivery.Address is null)
                unitOfWork.AddressRepository.Add(addressHolder);
            else
                unitOfWork.AddressRepository.Update(addressHolder);
        }


        
        var userUpdateData = new UpdateUserInfoDto
        {
            Name = deliveryDto.Name ,
            Phone = deliveryDto.Phone,
            Thumbnail = deliveryDto.Thumbnail,
            Password = deliveryDto.Password,
            NewPassword = deliveryDto.NewPassword,
        };


        if (deliveryDto.Thumbnail is not null)
        {
            var previuse = delivery.Thumbnail;
            if (previuse is not null)
                fileServices.DeleteFile(filePath: previuse);

            string? newThumbNail = null;
            newThumbNail = await fileServices.SaveFile(file: deliveryDto.Thumbnail, type: EnImageType.Delivery);
            delivery.Thumbnail = newThumbNail;

            unitOfWork.DeliveryRepository.Update(delivery);
        }

        if (userUpdateData.IsUpdateAnyFeild() is true)
        {
            await userServices.UpdateUser(userUpdateData, delivery!.UserId,true);
        }
        
        var result = await unitOfWork.SaveChanges();


        if (result < 1)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: "Something went wrong",
                isSuccessful: false,
                statusCode: 404
            );
        }


        delivery = await unitOfWork.DeliveryRepository.GetDelivery(delivery.Id);

        if (delivery is null)
        {
            return new Result<DeliveryDto>
            (
                data: null,
                message: "Delivery not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        return new Result<DeliveryDto>
        (
            data: delivery?.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }
}