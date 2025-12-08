namespace api.application.Interface.payment;

public interface IPayment
{
    Task<bool> IsValidPayment(string paymentId);
}