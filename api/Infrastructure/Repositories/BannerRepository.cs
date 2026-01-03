using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class BannerRepository(AppDbContext context) : IBannerRepository
{
    public void Add(Banner entity)
    {
        context
            .Banner
            .AddAsync(entity);
    }

    public void Update(Banner entity)
    {
        context
            .Banner
            .Update(entity);
    }
    
    public Task<int> GetBannerCount()
    {
        return context.Banner.CountAsync();
    }

    public Task<int> GetBannerCount(Guid storeId)
    {
        return context.Banner
            .Where(ba => ba.StoreId == storeId && ba.CreatedAt.AddHours(1) >= DateTime.Now)
            .CountAsync();
    }

    public void Delete(Guid id)
    {
        var banners = context
            .Banner
            .Where(ba => ba.Id == id)
            .ToListAsync();

        context.RemoveRange(banners);
    }

    public void Delete(List<Banner> banners)
    {
        context.Banner.RemoveRange(banners);
    }

    public async Task<Banner?> GetBanner(Guid id)
    {
        return await context
            .Banner
            .FindAsync(id);
    }

    public async Task<Banner?> GetBanner(Guid id, Guid storeId)
    {
        return await context
            .Banner
            .AsNoTracking()
            .FirstOrDefaultAsync(ba => ba.Id == id && ba.StoreId == storeId);
    }

    public async Task<List<Banner>> GetBanners(Guid id, int pageNumber, int pageSize)
    {
        return await context.Banner
            .OrderByDescending(ba => ba.CreatedAt)
            .Where(ba => ba.StoreId == id)
            .AsNoTracking()
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync();
    }

    public async Task<List<Banner>> GetBanners(int pageNumber, int pageSize)
    {
        return await context.Banner
            .OrderByDescending(ba => ba.CreatedAt)
            .AsNoTracking()
            .Skip((pageNumber - 1) * pageSize)
            .Take(pageSize)
            .ToListAsync();
    }

    public async Task<List<Banner>> GetBanners(int randomLenght)
    {
        return await context.Banner
            .OrderBy(ba => ba.Id)
            .AsNoTracking()
            .Take(randomLenght)
            .ToListAsync();
    }
}