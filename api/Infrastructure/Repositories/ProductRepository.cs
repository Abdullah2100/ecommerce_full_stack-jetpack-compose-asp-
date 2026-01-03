using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class ProductRepository(
    AppDbContext context
) : IProductRepository
{
    public async Task<IEnumerable<Product>> GetAllAsync(int page, int length)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVariants)
            .AsSplitQuery()
            .Skip((page - 1) * length)
            .Take(length)
            .OrderDescending()
            .ToListAsync();
    }


    public void Add(Product entity)
    {
        context.Products.Add(entity);
    }

    public void Update(Product entity)
    {
        var product = new Product()
        {
            Id = entity.Id,
            Name = entity.Name,
            Description = entity.Description,
            SubcategoryId = entity.SubcategoryId,
            Price = entity.Price,
            UpdatedAt = entity.UpdatedAt,
            Thumbnail = entity.Thumbnail,
            Symbol = entity.Symbol,
            StoreId = entity.StoreId
        };
        context.Products.Update(product);
    }

    public void Delete(Guid id)

    {
        var product = context.Products.Find(id);
        if (product == null) throw new ArgumentNullException();
        context.Products.Remove(product);
    }

    public void Delete(List<Product> products)
    {
        context.Products.RemoveRange(products);
    }

    public async Task<Product?> GetProduct(Guid id)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVariants)
            .AsSplitQuery()
            .FirstOrDefaultAsync(p => p.Id == id);
    }

    public async Task<Product?> GetProduct(Guid id, Guid storeId)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVariants)
            .AsSplitQuery()
            .FirstOrDefaultAsync(p => p.Id == id && p.StoreId == storeId);
    }

    public Task<int> GetProduct()
    {
        return context.Products.CountAsync();
    }

    public async Task<int?> GetProductPages()
    {
        return await context.Products.CountAsync();
    }

    public async Task<Product?> GetProductByUser(Guid id, Guid userId)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVariants)
            .AsSplitQuery()
            .FirstOrDefaultAsync(p => p.Id == id && p.Store.UserId == userId);
    }

    public async Task<IEnumerable<Product>> GetProducts(
        Guid storeId,
        Guid subCategoryId,
        int pageNum,
        int pageSize
    )
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVariants)
            .AsSplitQuery()
            .Where(p => p.StoreId == storeId && p.SubcategoryId == subCategoryId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<IEnumerable<Product>> GetProducts(
        Guid storeId,
        int pageNum,
        int pageSize)
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVariants)
            .AsSplitQuery()
            .Where(p => p.StoreId == storeId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }

    public async Task<IEnumerable<Product>> GetProducts(int page, int length)
    {
        try
        {
            var products = await context.Products
                .AsNoTracking()
                .Include(pro => pro.Store)
                .Include(pro => pro.SubCategory)
                .Include(pro => pro.ProductImages)
                .Include(pro => pro.ProductVariants)
                .AsSplitQuery()
                .Skip((page - 1) * length)
                .Take(length)
                .OrderDescending()
                .ToListAsync();
            if (products is null) return new List<Product>();

            for (int i = 0; i < products.Count; i++)
            {
                products[i].ProductVariants = await context.ProductVariants
                    .Include(pr => pr.Variant)
                    .Where(p => p.ProductId == products[i].Id).ToListAsync();
            }

            return products;
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex.Message);
            return new List<Product>();
        }
    }

    public async Task<IEnumerable<Product>> GetProducts(int randomNumber)
    {
        return await context
            .Products
            .OrderBy(x => Guid.NewGuid())
            .Take(randomNumber)
            .ToListAsync();
    }

    public async Task<IEnumerable<Product>> GetProductsByCategory(
        Guid categoryId,
        int pageNum,
        int pageSize
    )
    {
        return await context.Products
            .AsNoTracking()
            .Include(pro => pro.Store)
            .Include(pro => pro.SubCategory)
            .Include(pro => pro.ProductImages)
            .Include(pro => pro.ProductVariants)
            .AsSplitQuery()
            .Where(p => p.SubCategory.CategoryId == categoryId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
    }


    public async Task<bool> IsExist(Guid id)
    {
        return await context.Products.FindAsync(id) != null;
    }
}