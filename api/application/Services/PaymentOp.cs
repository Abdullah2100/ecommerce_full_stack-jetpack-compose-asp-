using api.application.Interface;
using api.application.Interface.payment;

namespace api.application.Services;

//this class is top level that use to when payment with any payment that saved in our system
public class PaymentOp(IPayment services)
{
    
    public Task<bool> IsValidPayment(string paymentId)
    {
        return services.IsValidPayment(paymentId);
    }
}