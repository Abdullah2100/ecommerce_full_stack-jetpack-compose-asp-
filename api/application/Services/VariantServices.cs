using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;
using api.util;

namespace api.application.Services;

public class VariantServices(IUnitOfWork unitOfWork)
    : IVariantServices
{
    public async Task<Result<VariantDto?>> CreateVariant(
        CreateVariantDto variantDto,
        Guid adminId
    )
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);

        var isValid = user.IsValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<VariantDto?>
            (
                data: null,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        if (await unitOfWork.VarientRepository.IsExist(variantDto.Name))
        {
            return new Result<VariantDto?>
            (
                data: null,
                message: "there are varient with the same name",
                isSuccessful: false,
                statusCode: 400
            );
        }

        Guid id = ClsUtil.GenerateGuid();

        Variant? varient = new Variant
        {
            Id = id,
            Name = variantDto.Name
        };

        unitOfWork.VarientRepository.Add(varient);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<VariantDto?>
            (
                data: null,
                message: "error while adding new varient",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<VariantDto?>
        (
            data: varient?.ToDto(),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<VariantDto?>> UpdateVariant(
        UpdateVariantDto variantDto,
        Guid adminId
    )
    {
        if (variantDto.IsEmpty())
            return new Result<VariantDto?>
            (
                data: null,
                message: "",
                isSuccessful: true,
                statusCode: 200
            );

        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);

        var isValid = user.IsValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<VariantDto?>
            (
                data: null,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        if (variantDto.Name is not null)
            if (await unitOfWork.VarientRepository.IsExist(variantDto.Name, variantDto.Id))
            {
                return new Result<VariantDto?>
                (
                    data: null,
                    message: "there are varient with the same name",
                    isSuccessful: false,
                    statusCode: 400
                );
            }

        Variant? varient = await unitOfWork.VarientRepository.GetVarient(variantDto.Id);

        if (varient is null)
        {
            return new Result<VariantDto?>
            (
                data: null,
                message: "varient not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        varient.Name = variantDto.Name ?? varient.Name;

        unitOfWork.VarientRepository.Update(varient);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<VariantDto?>
            (
                data: null,
                message: "error while update varient",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<VariantDto?>
        (
            data: varient?.ToDto(),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<bool>> DeleteVariant(Guid vairantId, Guid adminId)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);
        var isValid = user.IsValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }


        Variant? varient = await unitOfWork.VarientRepository.GetVarient(vairantId);

        if (varient is null)
        {
            return new Result<bool>
            (
                data: false,
                message: "varient not found",
                isSuccessful: false,
                statusCode: 404
            );
        }


        unitOfWork.VarientRepository
            .Delete(vairantId);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete varient",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<int?>> GetVariantPage(Guid adminId, int variantPerPage)

    {
        User? store = await unitOfWork.UserRepository.GetUser(adminId);
        
        var isValide = store.IsValidateFunc();

        if (isValide is not null)
            return new Result<int?>
            (
                data: null,
                message: "store not found",
                isSuccessful: false,
                statusCode: 404
            );
        var count = await unitOfWork.VarientRepository.GetVarientCount(variantPerPage);

        return new Result<int?>
        (
            data: count,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }


    public async Task<Result<List<VariantDto>>> GetVariants(int page, int pageSize)
    {
        List<VariantDto> varients = (await unitOfWork.VarientRepository
                .GetVarients(page, pageSize))
            .Select(va => va.ToDto())
            .ToList();
        return new Result<List<VariantDto>>
        (
            data: varients,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }
}