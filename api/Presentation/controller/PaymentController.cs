using api.Presentation.dto;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Stripe;
using Stripe.Checkout;

namespace api.Presentation.controller;

[Authorize]
[ApiController]
[Route("api/payment")]
public class PaymentController : ControllerBase
{
    [HttpPost("createCheckout")]
    public async Task<IActionResult> CreateSession([FromBody]PaymentRequirementData paymentRequirementData)
    {
        var options = new PaymentIntentCreateOptions
        {
            Amount = (long)Math.Ceiling(paymentRequirementData.Amount),
            Currency = "usd",
            AutomaticPaymentMethods = new PaymentIntentAutomaticPaymentMethodsOptions
            {
                Enabled = true,
            },
        };

        var service = new PaymentIntentService();
        var paymentIntent = await service.CreateAsync(options);

        return Ok(new { client_secret = paymentIntent.ClientSecret });
 
    }
}