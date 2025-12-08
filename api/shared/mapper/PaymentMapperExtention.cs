using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class PaymentMapperExtension
{
    public static CurrencyDto ToPaymentDto(this Currency currency)
    {
        return new CurrencyDto
        {
            Id = currency.Id,
            CreatedAt = currency.CreatedAt,
            UpdatedAt = currency.UpdatedAt,
            Name = currency.Name,
            Value= currency.Value,
            Symbol = currency.Symbol,
            IsDefault = currency.IsDefault
        };
    }
}