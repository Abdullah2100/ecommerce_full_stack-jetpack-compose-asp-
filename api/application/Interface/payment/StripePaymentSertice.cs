using ecommerc_dotnet.midleware.ConfigImplment;

namespace api.application.Interface.payment;

public class StripePayment (IConfig config) :IPayment
{
    
    public async Task<bool> IsValidPayment(string paymentId)
    {
        return true;
    }
}