using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;
using api.shared.signalr;
using api.util;
using ecommerc_dotnet.midleware.ConfigImplment;
using Microsoft.AspNetCore.SignalR;

namespace api.application.Services;

public class BannerSerivces(
    IConfig config,
    IHubContext<BannerHub> hubContext,
    IUnitOfWork unitOfWork,
    IFileServices fileServices)
    : IBannerSerivces
{
    public async Task<Result<BannerDto?>> CreateBanner(
        Guid userId,
        CreateBannerDto bannerDto
    )
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(userId);
        var validation = user.IsValidateFunc(false, true);
        if (validation is not null)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }


        //this to remove some banner to be away from overload of banner to keep vps fit to size
        int bannersCount = await unitOfWork.BannerRepository.GetBannerCount();
        if (bannersCount > 20)
        {
            var bannersRandom = await unitOfWork.BannerRepository.GetBanners(20);
            var imagesList = bannersRandom.Select(b => b.Image).ToList();
            fileServices.DeleteFile(imagesList);
            unitOfWork.BannerRepository.Delete(bannersRandom);
        }
        //end 


        //this for api  to prevent user have more that 20 banners
        int storeBannerCount = await unitOfWork.BannerRepository.GetBannerCount(user?.Store?.Id??ClsUtil.GenerateGuid());

        if (storeBannerCount >= 20 && user?.IsUser == true)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "store can only have 20",
                isSuccessful: false,
                statusCode: 400
            );
        }


        string? image = await fileServices.SaveFile(
            bannerDto.Image,
            EnImageType.Banner);

        if (image is null)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "error while saving banner  image",
                isSuccessful: false,
                statusCode: 400
            );
        }
        
        Banner banner = new Banner
        {
            Id = ClsUtil.GenerateGuid(),
            EndAt = bannerDto.EndAt,
            CreatedAt = DateTime.Today,
            Image = image,
            StoreId = user!.Store!.Id,
        };

        unitOfWork.BannerRepository.Add(banner);
        var result = await unitOfWork.SaveChanges();
        if (result == 0)
        {
            return new Result<BannerDto?>
            (
                data: null,
                message: "error while adding new banner",
                isSuccessful: false,
                statusCode: 400
            );
        }

        await hubContext.Clients.All.SendAsync("createdBanner", result);

        return new Result<BannerDto?>
        (
            data: banner.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<bool>> DeleteBanner(Guid id, Guid userId)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(userId);

        var validation = user.IsValidateFunc(false, true);
        if (validation is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }


        Banner? banner = await unitOfWork.BannerRepository
            .GetBanner(id);


        if (banner is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "banner  not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (banner.StoreId != user!.Store!.Id)
        {
            return new Result<bool>
            (
                data: false,
                message: "only store owner can delete banner",
                isSuccessful: false,
                statusCode: 404
            );
        }

        unitOfWork.BannerRepository.Delete(id);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while deleting banner",
                isSuccessful: false,
                statusCode: 400
            );
        }

        fileServices.DeleteFile(banner.Image);

        await hubContext.Clients.All.SendAsync("deletedOrder", id);


        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<List<BannerDto>>> GetBannersAll(
        Guid adminId,
        int pageNumber,
        int pageSize)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);
        var validation = user.IsValidateFunc();
        if (validation is not null)
        {
            return new Result<List<BannerDto>>
            (
                data: new List<BannerDto>(),
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }


        List<BannerDto> banners = (await unitOfWork.BannerRepository
                .GetBanners(pageNumber, pageSize))
            .Select(ba => ba.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<BannerDto>>
        (
            data: banners,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<BannerDto>>> GetBanners(
        Guid storeId,
        int pageNumber,
        int pageSize
    )
    {
        List<BannerDto> banners = (await unitOfWork.BannerRepository
                .GetBanners(storeId, pageNumber, pageSize))
            .Select(ba => ba.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<BannerDto>>
        (
            data: banners,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<BannerDto>>> GetBanners(
        int randomLenght
    )
    {
        List<BannerDto> banners = (await unitOfWork.BannerRepository
                .GetBanners(randomLenght))
            .Select(ba => ba.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<BannerDto>>
        (
            data: banners,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }
}