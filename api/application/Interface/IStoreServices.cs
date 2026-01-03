using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface IStoreServices
{
   public Task<Result<StoreDto?>> CreateStore(CreateStoreDto store, Guid userId);
   public Task<Result<StoreDto?>> UpdateStore(UpdateStoreDto storeDto, Guid userId);
   public Task<Result<StoreDto?>> GetStoreByUserId(Guid userId);
   public Task<Result<int?>> GetStorePage(Guid adminId, int storePerPage);
   public Task<Result<StoreDto?>> GetStoreByStoreId(Guid id);
   public Task<Result<List<StoreDto>?>> GetStores(Guid adminId, int pageNumber, int pageSize);
   public Task<Result<List<StoreDto>?>> GetStores(Guid adminId, string prefix, int pageSize);

   public Task<Result<bool?>> UpdateStoreStatus(Guid adminId, Guid storeId);
}