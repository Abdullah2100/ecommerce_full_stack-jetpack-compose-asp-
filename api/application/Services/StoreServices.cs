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

public class StoreServices(
    IWebHostEnvironment host,
    IConfig config,
    IFileServices fileServices,
    IUnitOfWork unitOfWork,
    IHubContext<StoreHub> hubContext

)
    : IStoreServices
{

    public async Task<Result<List<StoreDto>?>> GetStores(Guid adminId, string prefix, int pageSize)
    {
       /* User? user = await unitOfWork.UserRepository
            .GetUser(adminId);

        var isValide = user.IsValidateFunc(true);

        if (isValide is not null)
        {
            return new Result<List<StoreDto>?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }
        */


       var stores = (await unitOfWork.StoreRepository
               .GetStores(prefix, pageSize)
           );
        List<StoreDto> storeToDto = stores==null?new List<StoreDto>() :stores.Select(st => st.ToDto(config.getKey("url_file")))
            .ToList();
        
        return new Result<List<StoreDto>?>
        (
            data: storeToDto,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

 
    private void DeleteStoreImage(string? wallperper, string? smallImage)
    {
        if (wallperper is not null)
            fileServices.DeleteFile(wallperper);
        if (smallImage is not null)
            fileServices.DeleteFile(smallImage);
    }

    public async Task<Result<StoreDto?>> CreateStore(
        CreateStoreDto store,
        Guid userId)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(userId);

        var isValide = user.IsValidateFunc();

        if (isValide is not null)
        {
            return new Result<StoreDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }


        if (await unitOfWork.StoreRepository.IsExist(store.Name))
        {
            return new Result<StoreDto?>
            (
                data: null,
                message: "store already exist",
                isSuccessful: false,
                statusCode: 400
            );
        }

        string? wallperper = null, smallImage = null;

        smallImage = await fileServices.SaveFile(
            store.SmallImage,
            EnImageType.Store);
        wallperper = await fileServices.SaveFile(
            store.WallpaperImage,
            EnImageType.Store);


        if (smallImage is null || wallperper is null)
        {
            DeleteStoreImage(wallperper, smallImage);
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while saving store images",
                isSuccessful: false,
                statusCode: 400
            );
        }

        Guid id = ClsUtil.GenerateGuid();
        Store? storeData = new Store
        {
            Id = id,
            Name = store.Name,
            WallpaperImage = wallperper,
            SmallImage = smallImage,
            IsBlock = user?.Role != 0,
            UserId = userId,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
        };
        Address address = new Address
        {
            Id = ClsUtil.GenerateGuid(),
            IsCurrent = true,
            Latitude = store.Latitude,
            Longitude = store.Longitude,
            Title = store.Name,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
            OwnerId = id
        };

        unitOfWork.StoreRepository.Add(storeData);


        unitOfWork.AddressRepository.Add(address);


        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            DeleteStoreImage(wallperper, smallImage);
            fileServices.DeleteFile([wallperper, smallImage]);
            return new Result<StoreDto?>
            (
                data: null,
                message: "error while adding store",
                isSuccessful: false,
                statusCode: 400
            );
        }

        storeData = await unitOfWork.StoreRepository.GetStore(id)!;
        storeData!.Addresses = new List<Address> { address };


        return new Result<StoreDto?>
        (
            data: storeData?.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<StoreDto?>> UpdateStore(
        UpdateStoreDto storeDto,
        Guid userId
    )
    {
        if (storeDto.IsEmpty())
        {
            return new Result<StoreDto?>
            (
                data: null,
                message: "user not found",
                isSuccessful: true,
                statusCode: 200
            );
        }

        User? user = await unitOfWork.UserRepository
            .GetUser(userId);

        var isValide = user.IsValidateFunc(isStore: true);

        if (isValide is not null)
        {
            return new Result<StoreDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        if (storeDto.Name is not null)
        {
            bool isExist = await unitOfWork.StoreRepository.IsExist(storeDto.Name, user!.Store!.Id);

            if (isExist)
            {
                return new Result<StoreDto?>
                (
                    data: null,
                    message: "store already exist",
                    isSuccessful: false,
                    statusCode: 400
                );
            }
        }


        string? wallperper = null, smallImage = null;

        if (storeDto.WallpaperImage is not null)
        {
            wallperper = await fileServices.SaveFile(
                storeDto.WallpaperImage,
                EnImageType.Store);

            DeleteStoreImage(user!.Store!.WallpaperImage, null);
        }

        if (storeDto.SmallImage is not null)
        {
            smallImage = await fileServices.SaveFile(
                storeDto.SmallImage,
                EnImageType.Store);
            DeleteStoreImage(null, user!.Store?.SmallImage);
        }

        user!.Store!.SmallImage = smallImage ?? user!.Store!.SmallImage;
        user!.Store!.WallpaperImage = wallperper ?? user!.Store!.WallpaperImage;
        user!.Store!.Name = storeDto.Name ?? user!.Store!.Name;
        user!.Store!.UpdatedAt = DateTime.Now;

        unitOfWork.StoreRepository.Update(user!.Store!);

        if (
            (storeDto.Longitude is null && storeDto.Latitude is not null) ||
            (storeDto.Longitude is not null && storeDto.Latitude is null)
        )
        {
            return new Result<StoreDto?>(
                isSuccessful: false,
                data: null,
                message: "when update address you must change both longitude and latitude not one of them only ",
                statusCode: 400
            );
        }

        if (storeDto?.Longitude is not null && storeDto?.Latitude is not null)
        {
            Address? address = await unitOfWork.AddressRepository
                .GetAddressByOwnerId(user!.Store!.Id);

            if (address is null)
                return new Result<StoreDto?>
                (
                    data: null,
                    message: "store not has any address",
                    isSuccessful: false,
                    statusCode: 404
                );
            address.Title = storeDto?.Name ?? address.Title;
            address.UpdatedAt = DateTime.Now;
            address.Longitude = (decimal)storeDto?.Longitude!;
            address.Latitude = (decimal)storeDto!.Latitude;
            unitOfWork.AddressRepository.Update(address);
        }

        int result = await unitOfWork.SaveChanges();

        if (result < 1)
        {
            return new Result<StoreDto?>
            (
                data: null,
                message: "could not update store",
                isSuccessful: false,
                statusCode: 400
            );
        }

        Store? store = await unitOfWork.StoreRepository.GetStore(user.Store.Id);
        store.Addresses = await unitOfWork.AddressRepository.GetAllAddressByOwnerId(store!.Id);

        return new Result<StoreDto?>
        (
            data: store?.ToDto(config.getKey("url_file")),
            message: "error while update store Data",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<int?>> GetStorePage(Guid adminId, int storePerPage)

{
        User? store = await unitOfWork.UserRepository.GetUser(adminId);
        
        var isValide = store.IsValidateFunc();

        if (isValide is not null)
            return new Result<int?>
            (
                data: null,
                message: "store not found",
                isSuccessful: false,
                statusCode: 404
            );
        var count = await unitOfWork.StoreRepository.GetStoresCount(storePerPage);

        return new Result<int?>
        (
            data: count,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<StoreDto?>> GetStoreByUserId(Guid userId)
    {
        Store? store = await unitOfWork.StoreRepository.GetStoreByUserId(userId);

        if (store is null)
            return new Result<StoreDto?>
            (
                data: null,
                message: "store not found",
                isSuccessful: false,
                statusCode: 404
            );

        return new Result<StoreDto?>
        (
            data: store.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<StoreDto?>> GetStoreByStoreId(Guid id)
    {
        Store? store = await unitOfWork.StoreRepository.GetStore(id);

        if (store is null)
            return new Result<StoreDto?>
            (
                data: null,
                message: "store not found",
                isSuccessful: false,
                statusCode: 404
            );

        return new Result<StoreDto?>
        (
            data: store.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<List<StoreDto>?>> GetStores(Guid adminId, int pageNumber, int pageSize)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);

        var isValide = user.IsValidateFunc(true);

        if (isValide is not null)
        {
            return new Result<List<StoreDto>?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        List<StoreDto> stores = (await unitOfWork.StoreRepository
                .GetStores(pageNumber, pageSize)
            ).Select(st => st.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<StoreDto>?>
        (
            data: stores,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool?>> UpdateStoreStatus(Guid adminId, Guid storeId)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);

        var isValide = user.IsValidateFunc(true);

        if (isValide is not null)
        {
            return new Result<bool?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        Store? store = await unitOfWork.StoreRepository.GetStore(storeId);

        if (store is null)
            return new Result<bool?>
            (
                data: null,
                message: "store not found",
                isSuccessful: false,
                statusCode: 400
            );

        isValide = store.user.IsValidateFunc(true);

        if (isValide is null && store.UserId != user!.Id)
        {
            return new Result<bool?>(
                isSuccessful: false,
                data: null,
                message: "only Admin can update his store Status",
                statusCode: 400
            );
        }


        store.IsBlock = !store.IsBlock;

        if (store.IsBlock == true && user?.Role == 0)
        {
            return new Result<bool?>(
                isSuccessful: false,
                data: null,
                message: "this store is belong to admin you could not block it ",
                statusCode: 400
            ); 
        }
        
        unitOfWork.StoreRepository.Update(store);
        int result = await unitOfWork.SaveChanges();
        if (result == 0)
            return new Result<bool?>
            (
                data: null,
                message: "error while update store status",
                isSuccessful: false,
                statusCode: 400
            );
        
        await hubContext.Clients.All.SendAsync("storeStatus", new StoreStatusDto
        {
            StoreId = storeId,
            Status = true
        });
        return new Result<bool?>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }
}