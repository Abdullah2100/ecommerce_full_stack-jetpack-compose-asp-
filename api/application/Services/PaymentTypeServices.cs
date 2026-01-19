using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Presentation.dto;
using api.Infrastructure;
using api.shared.mapper;
using api.util;
using ecommerc_dotnet.midleware.ConfigImplment;

namespace api.application.Services;

public class PaymentTypeServices(
    IUnitOfWork unitOfWork,
    IFileServices fileServices,
    IConfig config
    ):IPaymentTypeServices
{
    public async Task<Result<PaymentTypeDto?>> Create(CreatePaymentTypeDto paymentTypeDto,Guid adminId)
    {
        User? admin = await unitOfWork.UserRepository.GetUser(adminId);
        var validation = admin.IsValidateFunc();
        if (validation is not null)
        {
            return new Result<PaymentTypeDto?>
            (
                data: null,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }

        string? thembnail = await fileServices.SaveFile(paymentTypeDto.Thumbnail, EnImageType.Payment);

        if (thembnail is null)
        {
            return new Result<PaymentTypeDto?>
            (
                data: null,
                message: "could not save payement image to api",
                isSuccessful: false,
                statusCode: 404 
            ); 
        }

        PaymentType paymentType = new PaymentType()
        {
            Id = ClsUtil.GenerateGuid(),
            IsHashCheckOperation = paymentTypeDto.IsHashCheckOperation,
            Name = paymentTypeDto.Name,
            Thumbnail = thembnail,
            UserId = adminId
        };
        
        unitOfWork.PaymentTypeRepository.Add(paymentType);
        var result = await unitOfWork.SaveChanges();
       
        if (result == 0)
        {
            return new Result<PaymentTypeDto?>
            (
                data: null,
                message: "could not save payment type to system",
                isSuccessful: false,
                statusCode: 404 
            ); 
        }

        var paymentDto = paymentType.ToDto(config.getKey("url_file"));
        return new Result<PaymentTypeDto?>
        (
            data: paymentDto,
            message: "",
            isSuccessful: false,
            statusCode: 201
        ); 
    }

    public async Task<Result<PaymentTypeDto?>> Update(UpdatePaymentTypeDto paymentTypeDto,Guid adminId)
    {
        User? admin = await unitOfWork.UserRepository.GetUser(adminId);
        var validation = admin.IsValidateFunc();
        if (validation is not null)
        {
            return new Result<PaymentTypeDto?>
            (
                data: null,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }

        var paymentType = await unitOfWork.PaymentTypeRepository.GetPaymentTypeGetPayment(paymentTypeDto.Id);

        if (paymentType is null)
        {
            return new Result<PaymentTypeDto?>
            (
                data: null,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            ); 
        }
        
        var isAlreadyExist = await unitOfWork.PaymentTypeRepository.IsExistPaymentType(paymentTypeDto.Name, paymentTypeDto.Id);
        
        if (isAlreadyExist== true)
        {
            return new Result<PaymentTypeDto?>
            (
                data: null,
                message: "this payment type name is  already in use ",
                isSuccessful: false,
                statusCode: 400
            ); 
        }
        

        string? thembnail = paymentTypeDto.Thumbnail == null ? null: await fileServices.SaveFile(paymentTypeDto.Thumbnail, EnImageType.Payment);

        paymentType.Name =  paymentTypeDto.Name ?? paymentType.Name;
        paymentType.Thumbnail =  thembnail ?? paymentType.Thumbnail;
        paymentType.IsHashCheckOperation =  paymentTypeDto.IsHashCheckOperation?? paymentType.IsHashCheckOperation;
        
        unitOfWork.PaymentTypeRepository.Update(paymentType);
        var result = await unitOfWork.SaveChanges();
       
        if (result == 0)
        {
            return new Result<PaymentTypeDto?>
            (
                data: null,
                message: "could not update payment type to system",
                isSuccessful: false,
                statusCode: 404 
            ); 
        }

        var paymentDto = paymentType.ToDto(config.getKey("url_file"));
        return new Result<PaymentTypeDto?>
        (
            data: paymentDto,
            message: "",
            isSuccessful: false,
            statusCode: 200
        ); 
    }

    public async Task<Result<List<PaymentTypeDto>?>> GetPaymentTypes(sbyte pageNum,sbyte pageSie=25)
    {
        var paymentTypes = await unitOfWork.PaymentTypeRepository.GetPaymentTypes(pageNum, pageSie);

        var paymentTypesToDtos = paymentTypes.Select(s => s.ToDto(config.getKey("url_file"))).ToList();
        return new Result<List<PaymentTypeDto>?>
        (
            data: paymentTypesToDtos,
            message: "",
            isSuccessful: false,
            statusCode: 200
        ); 
    }
}