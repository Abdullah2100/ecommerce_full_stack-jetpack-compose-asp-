using api.application.Interface;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.application.Result;
using api.shared.mapper;
using api.util;
using ecommerc_dotnet.midleware.ConfigImplment;

namespace api.application.Services;

public class CurrencyServices(IUnitOfWork unitOfWork) : ICurrencyServices
{
    
    public async Task<Result<CurrencyDto?>> CreateCurrency(Guid adminId, CreateCurrencyDto currencyDto)
    {
        User? admin = await unitOfWork.UserRepository.GetUser(adminId);

        var isValide = admin.IsValidateFunc(true);

        if (isValide is not null)
        {
            return new Result<CurrencyDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        Currency currency = new Currency
        {
            Id = ClsUtil.GenerateGuid(),
            CreatedAt = DateTime.Now,
            Symbol = currencyDto.Symbol,
            Name = currencyDto.Name,
            Value = currencyDto.Value,
            IsDefault =currencyDto.IsDefault 
        };
        unitOfWork.CurrencyRepository.Add(currency);
        var result = await unitOfWork.SaveChanges();
        
        if (result == 0)
        {
            return new Result<CurrencyDto?>(
                isSuccessful: false,
                data: null,
                message: "Could not Save Payment Image",
                statusCode: 400
            );
        }

        CurrencyDto? paymentToDto = currency.ToPaymentDto();
        return new Result<CurrencyDto?>(
            isSuccessful: true,
            data: paymentToDto,
            message: "",
            statusCode: 201
        );
    }

    public async Task<Result<CurrencyDto?>> UpdateCurrency(Guid adminId, UpdateCurrencyDto currencyDto)
    {
        User? admin = await unitOfWork.UserRepository.GetUser(adminId);

        var isValide = admin.IsValidateFunc(true);

        if (isValide is not null)
        {
            return new Result<CurrencyDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        Currency? currency = await unitOfWork.CurrencyRepository.GetCurrency(currencyDto.Id);

        if (currency is null)
        {
            return new Result<CurrencyDto?>(
                isSuccessful: false,
                data: null,
                message: "payment is not found",
                statusCode: 404
            );
        }


        currency.Name = currencyDto.Name ?? currency.Name;
        currency.Symbol = currencyDto.Symbol ?? currency.Symbol;
        currency.Value = currencyDto.Value??currency.Value;
        currency.UpdatedAt = DateTime.Now;

        unitOfWork.CurrencyRepository.Update(currency);
        var result = await unitOfWork.SaveChanges();
        if (result == 0)
        {
            return new Result<CurrencyDto?>(
                isSuccessful: false,
                data: null,
                message: "Could not Save Payment Image",
                statusCode: 400
            );
        }

        return new Result<CurrencyDto?>(
            isSuccessful: true,
            data: currency.ToPaymentDto(),
            message: "",
            statusCode: 200
        );
    }

    public async Task<Result<bool>> DeleteCurrency(Guid adminId, Guid id)
    {
        User? admin = await unitOfWork.UserRepository.GetUser(adminId);

        var isValide = admin.IsValidateFunc(true);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSuccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        Currency? payment = await unitOfWork.CurrencyRepository.GetCurrency(id);

        if (payment is null)
        {
            return new Result<bool>(
                isSuccessful: false,
                data: false,
                message: "payment is not found",
                statusCode: 404
            );
        }


        await unitOfWork.CurrencyRepository.Delete(payment.Id);
        var result = await unitOfWork.SaveChanges();
        if (result == 0)
        {
            return new Result<bool>(
                isSuccessful: false,
                data: false,
                message: "Could not delete Payment",
                statusCode: 400
            );
        }


        return new Result<bool>(
            isSuccessful: true,
            data: true,
            message: "",
            statusCode: 200);
    }

    public async Task<Result<List<CurrencyDto>>> GetCurrency(int pageNum,int pageSize)
    {
             
        var payments = await unitOfWork.CurrencyRepository.GetAll(pageNum, pageSize);
        
        var paymentToDto = payments.Select(payment => payment.ToPaymentDto()).ToList();
        return new Result<List<CurrencyDto>>(
            isSuccessful: true,
            data: paymentToDto,
            message:"", 
            statusCode:200 
        ); 
    }
}