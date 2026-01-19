using api.domain.entity;
using api.Presentation.dto;

namespace api.shared.mapper;

public static class PaymentTypeMapperExtension
{
    extension(PaymentType paymentType)
    {
        public PaymentTypeDto ToDto(String url)
        {
            return new PaymentTypeDto()
            {
                Id = paymentType.Id,
                Name = paymentType.Name,
                IsHashCheckOperation = paymentType.IsHashCheckOperation,
                Thumbnail =url+ paymentType.Thumbnail
            };
        }
    }
}