using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;
using api.util;

namespace api.application.Services;

public class GeneralSettingServices(
    IUnitOfWork unitOfWork
)
    : IGeneralSettingServices
{
    public async Task<Result<GeneralSettingDto?>> CreateGeneralSetting(
        Guid adminId,
        GeneralSettingDto settingDto)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);

        var validation = user.IsValidateFunc();
        if (validation is not null)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }

        if (await unitOfWork.GeneralSettingRepository.IsExist(settingDto.Name))
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "there are  generalsetting with the same name",
                isSuccessful: false,
                statusCode: 400
            );
        }

        GeneralSetting generalSetting = new GeneralSetting
        {
            CreatedAt = DateTime.Now,
            Id = ClsUtil.GenerateGuid(),
            Name = settingDto.Name,
            Value = settingDto.Value
        };
        unitOfWork.GeneralSettingRepository.Add(generalSetting);
        int result = await unitOfWork.SaveChanges();


        if (result == 0)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "error while adding generalsetting",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<GeneralSettingDto?>
        (
            data: generalSetting?.ToDto(),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<GeneralSettingDto?>> UpdateGeneralSetting(
        Guid id, Guid adminId,
        UpdateGeneralSettingDto settingDto
    )
    {
        if (settingDto.IsEmpty())
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "no change found",
                isSuccessful: false,
                statusCode: 200
            );

        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);

        var validation = user.IsValidateFunc();
        if (validation is not null)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }

        GeneralSetting? generalSetting = await unitOfWork.GeneralSettingRepository.GetGeneralSetting(id);
        if (generalSetting is null)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "no generalsetting found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        generalSetting.Name = settingDto.Name ?? generalSetting.Name;
        generalSetting.Value = settingDto.Value ?? generalSetting.Value;
        generalSetting.UpdatedAt = DateTime.Now;

        unitOfWork.GeneralSettingRepository.Add(generalSetting);
        int result = await unitOfWork.SaveChanges();


        if (result == 0)
        {
            return new Result<GeneralSettingDto?>
            (
                data: null,
                message: "error while update generalsetting",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<GeneralSettingDto?>
        (
            data: generalSetting?.ToDto(),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> DeleteGeneralSetting(Guid id, Guid adminId)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);
        var validation = user.IsValidateFunc();
        if (validation is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }

        if (!(await unitOfWork.GeneralSettingRepository.IsExist(id)))
        {
            return new Result<bool>
            (
                data: false,
                message: "generalSetting not found",
                isSuccessful: false,
                statusCode: 400
            );
        }

        unitOfWork.GeneralSettingRepository.Delete(id);
        int result = await unitOfWork.SaveChanges();


        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete generalsetting",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<bool>
        (
            data: false,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<List<GeneralSettingDto>>> GetGeneralSettings(int pageNum, int pageSize)
    {
        List<GeneralSettingDto> categories = (await unitOfWork.GeneralSettingRepository.Getgenralsettings(pageNum, pageSize))
            .Select(ca => ca.ToDto())
            .ToList();
        return new Result<List<GeneralSettingDto>>
        (
            data: categories,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }
}