using api.domain.entity;

namespace api.domain.Interface;

public interface IProductRepository:IRepository<Product>
{
    
    Task<Product?> GetProduct(Guid id);
    Task<Product?> GetProduct(Guid id, Guid storeId);
    Task<int> GetProduct();
    Task<int?> GetProductPages();
    Task<Product?> GetProductByUser(Guid id,Guid userId);
    
    Task<IEnumerable<Product>> GetProducts(Guid storeId,Guid subCategoryId,int pageNum,int pageSize);
    Task<IEnumerable<Product>> GetProducts(Guid storeId,int pageNum,int pageSize);
    Task<IEnumerable<Product>> GetProducts(int page,int length);
    Task<IEnumerable<Product>> GetProducts(int randomNumber);
    Task<IEnumerable<Product>> GetProductsByCategory(Guid categoryId,int pageNum,int pageSize);
    
    Task<bool> IsExist(Guid id);
    void Delete(Guid id);
    void Delete(List<Product> products);

}