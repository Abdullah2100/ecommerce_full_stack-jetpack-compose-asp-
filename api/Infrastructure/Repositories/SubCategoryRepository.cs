using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class SubCategoryRepository(AppDbContext context) : ISubCategoryRepository
{
    public async Task<SubCategory?> GetSubCategory(Guid id)
    {
       return await context.SubCategories.FindAsync(id);
    }

    public async Task<List<SubCategory>> GetSubCategories(
        Guid storeId,
        int  pageNumber,
        int pageSize
        )
    {
        return await context
            .SubCategories
            .AsNoTracking()
            .Where(su=>su.StoreId==storeId)
            .Skip((pageNumber - 1) * pageSize)
            .OrderDescending()
            .Take(pageSize)
            .ToListAsync();
    }
    
    public async Task<List<SubCategory>> GetSubCategories(
        int  pageNumber,
        int pageSize
    )
    {
        return await context
            .SubCategories
            .AsNoTracking()
            .Skip((pageNumber - 1) * pageSize)
            .OrderDescending()
            .Take(pageSize)
            .ToListAsync();
    }

    public async Task<int> GetSubCategoriesCount(Guid storeId)
    {
        return await context
            .SubCategories
            .AsNoTracking()
            .Where(su => su.StoreId == storeId)
            .CountAsync();
    }

    public async Task<bool> IsExist(Guid id)
    {
        return await context.SubCategories.AsNoTracking().AnyAsync(x=>x.Id==id)==true;

    }

    public async Task<bool> IsExist(Guid storeId, string name)
    {
        return await context.SubCategories
            .AsNoTracking()
            .AnyAsync(su => su.StoreId == storeId && su.Name == name);
    }

    public async Task<bool> IsExist(Guid storeId, Guid id)
    {
        return await context.SubCategories
            .AsNoTracking()
            .AnyAsync(su => su.StoreId == storeId && su.Id == id);

    }

    public async Task<IEnumerable<SubCategory>> getAllAsync(int page, int length)
    {
        return await context
            .SubCategories
            .AsNoTracking()
            .Skip((page - 1) * length)
            .OrderDescending()
            .Take(length)
            .ToListAsync(); 
    }

    public void  Add(SubCategory entity)
    {
         context.SubCategories.Add(entity);
    }

    public void Update(SubCategory entity)
    {
        context.SubCategories.Update(entity);
    }

    public  void  Delete(Guid id)
    {
         var subcategories=context.SubCategories.Where(su => su.Id == id)
            .ToList();
         context.SubCategories.RemoveRange(subcategories);
    }
}