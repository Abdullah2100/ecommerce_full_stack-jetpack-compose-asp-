using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class CategoryRepository(AppDbContext context) : ICategoryRepository
{
 

    public void  Add(Category entity)
    {
         context.Categories.Add(entity);
    }

    public void  Update(Category entity)
    {
        context.Categories.Update(entity);
    }
    

    public async Task<Category?> GetCategory(Guid id)
    {
        return await context.Categories.FindAsync(id);
    }

    public async Task<List<Category>> GetCategories(int page, int length)
    {
        return await context
            .Categories
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .OrderDescending()
            .ToListAsync(); 
    }

    public async Task<int> GetCategoriesCount()
    {
       return await context.Categories.CountAsync();
    }

    public async Task<List<Category>> GetCategories(int randomNumber)
    {
       return await context
           .Categories
           .OrderBy(x => Guid.NewGuid())
           .Take(randomNumber)
           .ToListAsync();
    }

    public async Task<bool> IsExist(Guid id)
    {
        return await context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Id == id);
    }

    public async Task<bool> IsExist(string name)
    {
        return await context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Name == name);
    }

    public async Task<bool> IsExist(string name,Guid id)
    {
        return await context
            .Categories
            .AsNoTracking()
            .AnyAsync(e => e.Name == name && e.Id != id);
    }
    
    public  void Delete(Guid id)
    {
        var category= context.Categories.FirstOrDefault(ca => ca.Id == id);
        if (category is null) throw new ArgumentNullException();
        context.Categories.Remove(category);
    }

    public void Delete(List<Category> categories)
    {
       context.Categories.RemoveRange(categories); 
    }
}