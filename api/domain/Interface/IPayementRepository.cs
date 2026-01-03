using api.domain.entity;

namespace api.domain.Interface;

public interface ICurrencyRepository : IRepository<Currency>
{
    Task<Currency?> GetCurrencies(Guid id );
    Task<List<Currency>> GetCurrencies(int randomNumber);
    Task<int> GetCurrenciesCount();
    Task<List<Currency>> GetAll(int pageNum,int pageSize);
    Task Delete(Guid id);
    void Delete(List<Currency> currencies);
    Task<bool> isExist(string symbol);
}