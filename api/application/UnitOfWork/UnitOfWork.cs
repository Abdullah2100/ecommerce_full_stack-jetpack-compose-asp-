using api.domain.Interface;
using api.Infrastructure;
using api.Infrastructure.Repositories;

namespace api.application.UnitOfWork;

public class UnitOfWork : IUnitOfWork
{
    private readonly AppDbContext _context;

    public UnitOfWork(
        AppDbContext context
    )
    {
        _context = context;
        AddressRepository= new AddressRepository( _context );
        BannerRepository = new BannerRepository( _context );
        CategoryRepository = new CategoryRepository( _context );
        DeliveryRepository = new DeliveryRepository( _context );
        GeneralSettingRepository = new GeneralSettingRepository( _context );
        OrderItemRepository = new OrderItemRepository( _context );
        ProductVariantRepository =  new ProductVariantRepository( _context );
        ProductImageRepository = new ProductImageRepository( _context );
        ProductRepository = new ProductRepository(context);
        OrderRepository = new OrderRepository( _context);
        PasswordRepository = new ReseatPasswordRepository( _context );
        StoreRepository = new StoreRepository( _context );
        SubCategoryRepository = new SubCategoryRepository( _context );
        UserRepository = new UserRepository( _context );
        VarientRepository =  new VarientRepository( _context );
        OrderProductVariantRepository = new OrderProductVariantRepository(context);
        AnalyseRepository = new AnalyseRepository( _context );
        CurrencyRepository = new CurrencyRepository( _context );
        PaymentTypeRepository = new  PaymentTypeRepository( _context );
    }
    
    public void Dispose()
    {
        _context.Dispose();
    }

    public IOrderProductVariant OrderProductVariantRepository { get; }

    public ICurrencyRepository CurrencyRepository { get; }
    public IPaymentTypeRepository PaymentTypeRepository { get; set; }

    public async Task<int> SaveChanges()
    {
        try
        {
            return await _context.SaveChangesAsync();

        }
        catch (Exception ex)
        {
            Console.WriteLine($"this the exption error {ex.Message}");
            return 0;
        }
    }

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
    public IAnalyseRepository AnalyseRepository { get; }
}