using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class PaymentTypeRepository(AppDbContext context):IPaymentTypeRepository
{
    public void Add(PaymentType entity)
    {
           context.PaymentTypes.Add(entity);
    }

    public void Update(PaymentType entity)
    {
        context.PaymentTypes.Update(entity);
    }

    public async Task<PaymentType?> GetPaymentTypeGetPayment(Guid id)
    {
        return await context.PaymentTypes.AsNoTracking().FirstOrDefaultAsync(x => x.Id == id);
    }
    
    public async Task<List<PaymentType>> GetPaymentTypes(sbyte pageNum,sbyte pageSie)
    {
        return await context.PaymentTypes.AsNoTracking()
            .Take(pageSie)
            .Skip((pageNum - 1) * pageSie)
            .ToListAsync();
    }

    public async Task<bool> IsExistPaymentType(string name,Guid id)
    {
        return await context.PaymentTypes.AsNoTracking().AnyAsync(x => x.Name == name && x.Id != id);

    }
}