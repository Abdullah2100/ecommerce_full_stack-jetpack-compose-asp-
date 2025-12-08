using api.domain.Interface;

namespace api.Infrastructure;

public interface IUnitOfWork:IDisposable
{
    public IAddressRepository AddressRepository { get; }
    public IBannerRepository BannerRepository { get; }
    public ICategoryRepository CategoryRepository { get; }
    public IDeliveryRepository DeliveryRepository { get; }
    public IGeneralSettingRepository GeneralSettingRepository { get; }
    public IOrderItemRepository OrderItemRepository { get; }
    public IOrderRepository OrderRepository { get; }
    public IProductRepository ProductRepository { get; }
    public IProductImageRepository ProductImageRepository { get; }
    public IProductVariantRepository ProductVariantRepository { get; }
    public IReseatePasswordRepository PasswordRepository { get; }
    public IStoreRepository StoreRepository { get; }
    public ISubCategoryRepository SubCategoryRepository { get; }
    public IUserRepository UserRepository { get; }
    public IVarientRepository VarientRepository { get; }
    public IOrderProductVariant OrderProductVariantRepository { get; } 
    public IAnalyseRepository AnalyseRepository { get; }
    public ICurrencyRepository CurrencyRepository { get; }


    public Task<int> SaveChanges();
}