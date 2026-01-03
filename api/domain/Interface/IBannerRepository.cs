using api.domain.entity;

namespace api.domain.Interface;

public interface IBannerRepository:IRepository<Banner>
{
    Task<Banner?> GetBanner(Guid id);
    Task<Banner?> GetBanner(Guid id,Guid storeId);
    
    Task<List<Banner>> GetBanners(Guid id,int pageNumber ,int pageSize);
    Task<List<Banner>> GetBanners(int pageNumber ,int pageSize);
    Task<List<Banner>> GetBanners(int randomLenght);
    Task<int> GetBannerCount();
    Task<int> GetBannerCount(Guid storeId);


    void Delete(Guid id);

    void  Delete(List<Banner> banners);

}