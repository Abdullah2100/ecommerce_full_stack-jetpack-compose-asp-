using api.domain.entity;

namespace api.domain.Interface;

public interface ICurrencyRepository : IRepository<Currency>
{
    Task<Currency?> GetCurrency(Guid id );
    Task<List<Currency>> GetAll(int pageNum,int pageSize);
    Task Delete(Guid id);
    Task<bool> isExist(string symbol);
}