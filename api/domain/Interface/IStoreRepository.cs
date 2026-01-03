using api.domain.entity;

namespace api.domain.Interface;

public interface IStoreRepository:IRepository<Store>
{
    Task<Store?> GetStore(Guid id);
    Task<Store?> GetStoreByUserId(Guid id);
    Task<List<Store>> GetStores(int page,int length);
    Task<List<Store>> GetStores(string prefix,int length);

    Task<int> GetStoresCount(int storePerPage);
    Task<bool> IsExist(string name);
    Task<bool> IsExist(string name,Guid id);
    Task<bool> IsExist(Guid id);
    Task<bool> IsExist(Guid id,Guid subCategoryId);
    void Delete(Guid id);
}