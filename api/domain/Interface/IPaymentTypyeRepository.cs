using api.domain.entity;

namespace api.domain.Interface;

public interface IPaymentTypeRepository:IRepository<PaymentType>
{
   public Task<PaymentType?> GetPaymentTypeGetPayment(Guid id);
   public Task<List<PaymentType>> GetPaymentTypes(sbyte pageNum,sbyte pageSie);
   public Task<bool> IsExistPaymentType(string name,Guid id);
}