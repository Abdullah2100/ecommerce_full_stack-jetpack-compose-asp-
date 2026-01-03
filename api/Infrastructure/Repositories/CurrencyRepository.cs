using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class CurrencyRepository(AppDbContext context):ICurrencyRepository
{
    public void Add(Currency entity)
    {
        context.Add(entity);
    }

    public void Update(Currency entity)
    {
        context.Update(entity);
    }
    
    public async Task<Currency?> GetCurrencies(Guid id )
    {
        Currency? element = await context.Payments
            .AsNoTracking()
            .FirstOrDefaultAsync(x=>x.Id == id);
    return element;
    }

    public Task<List<Currency> > GetCurrencies(int randomNumber)
    {
       return context.Currencies
           .OrderBy(x => Guid.NewGuid())
           .Take(randomNumber)
           .ToListAsync();
    }

    public Task<int> GetCurrenciesCount()
    {
        return context.Currencies.CountAsync();
    }

    public async Task<List<Currency>> GetAll(int pageNum,int pageSize)
    {
        return await context.Payments
            .AsNoTracking()
            .Skip((pageNum-1)*pageSize)
            .Take(pageSize)
            .ToListAsync();
    }

    public async Task Delete(Guid id)
    {
        Currency? element = await context.Payments.AsNoTracking().FirstOrDefaultAsync(x => x.Id == id);

        if (element is null) return;
        context.Payments.Remove(element);
    }

    public void Delete(List<Currency> currencies)
    {
        context.Currencies.RemoveRange(currencies);
    }

    public async Task<bool> isExist(string symbol)
    {
       return await context.Payments
           .AsNoTracking()
           .AnyAsync(x => x.Symbol == symbol);
    }
}