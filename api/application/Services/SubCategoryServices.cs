using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;
using api.util;

namespace api.application.Services;

public class SubCategoryServices(
    IUnitOfWork unitOfWork)
    : ISubCategoryServices
{
    public async Task<Result<SubCategoryDto?>> CreateSubCategory(
        Guid userId,
        CreateSubCategoryDto subCategoryDto
    )
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(userId);

        var isValide = user.IsValidateFunc(isAdmin: false, isStore: true);

        if (isValide is not null)
        {
            return new Result<SubCategoryDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }


        int count = await unitOfWork.SubCategoryRepository.GetSubCategoriesCount(user.Store.Id);

        if (count == 20)
        {
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "store can maximum 20 subcategories",
                isSuccessful: false,
                statusCode: 400
            );
        }

        Guid id = ClsUtil.GenerateGuid();

        SubCategory subCategory = new SubCategory
        {
            Id = id,
            CategoryId = subCategoryDto.CategoryId,
            StoreId = user.Store.Id,
            Name = subCategoryDto.Name,
            UpdatedAt = null,
            CreatedAt = DateTime.Now,
        };

        unitOfWork.SubCategoryRepository.Add(subCategory);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "error while adding new subcategory",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<SubCategoryDto?>
        (
            data: subCategory.ToDto(),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<SubCategoryDto?>> UpdateSubCategory(
        Guid userId,
        UpdateSubCategoryDto subCategoryDto
    )
    {
        if (subCategoryDto.IsEmpty())
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "",
                isSuccessful: true,
                statusCode: 200
            );


        User? user = await unitOfWork.UserRepository
            .GetUser(userId);

        var isValide = user.IsValidateFunc(isStore: true);

        if (isValide is not null)
        {
            return new Result<SubCategoryDto?>(
                isSuccessful: false,
                data: null,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        SubCategory? subCategory = await unitOfWork.SubCategoryRepository
            .GetSubCategory(subCategoryDto.Id);

        if (subCategory is null || subCategory.StoreId != user!.Store!.Id)
        {
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "subcategory not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (subCategoryDto.CategoryId is not null &&
            !(await unitOfWork.CategoryRepository.IsExist((Guid)subCategoryDto!.CategoryId))
           )
        {
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "invalide category",
                isSuccessful: false,
                statusCode: 404
            );
        }

        subCategory.Name = subCategoryDto.Name ?? subCategory.Name;
        subCategory.CategoryId = subCategoryDto.CategoryId ?? subCategory.CategoryId;
        subCategory.UpdatedAt = DateTime.Now;

        unitOfWork.SubCategoryRepository.Update(subCategory);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<SubCategoryDto?>
            (
                data: null,
                message: "error while update subcategory",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<SubCategoryDto?>
        (
            data: subCategory.ToDto(),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> DeleteSubCategory(Guid id, Guid userId)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(userId);

        var isValide = user.IsValidateFunc(isStore: true);

        if (isValide is not null)
        {
            return new Result<bool>(
                isSuccessful: false,
                data: false,
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }

        SubCategory? subCategory = await unitOfWork.SubCategoryRepository
            .GetSubCategory(id);

        if (subCategory is null || subCategory.StoreId != user!.Store!.Id)
        {
            return new Result<bool>
            (
                data: false,
                message: "subcategory not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        unitOfWork.SubCategoryRepository.Delete(id);
        int result = await unitOfWork.SaveChanges();
        
        if (result == 0)
            return new Result<bool>
            (
                data: false,
                message: "error while deleting subcategory",
                isSuccessful: false,
                statusCode: 404
            );

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }

    public async Task<Result<List<SubCategoryDto>>> GetSubCategories(Guid id, int page, int length)
    {
        List<SubCategoryDto> subCategories = (await unitOfWork.SubCategoryRepository
                .GetSubCategories(id, page, length))
            .Select(su => su.ToDto())
            .ToList();
        return (subCategories.Count > 0) switch
        {
            true =>
                new Result<List<SubCategoryDto>>
                (
                    data: subCategories,
                    message: "",
                    isSuccessful: true,
                    statusCode: 200
                ),
            _ => new Result<List<SubCategoryDto>>
            (
                data: new List<SubCategoryDto>(),
                message: "",
                isSuccessful: true,
                statusCode: 204
            )
        };
    }

    public async Task<Result<List<SubCategoryDto>>> GetSubCategoryAll(
        Guid adminId,
        int page,
        int length)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);
        var isValide = user.IsValidateFunc(isAdmin: true);

        if (isValide is not null)
        {
            return new Result<List<SubCategoryDto>>(
                isSuccessful: false,
                data: new List<SubCategoryDto>(),
                message: isValide.Message,
                statusCode: isValide.StatusCode
            );
        }


        List<SubCategoryDto> subcategories = (await unitOfWork.SubCategoryRepository
                .GetSubCategories(page, length))
            .Select(ba => ba.ToDto())
            .ToList();

        return new Result<List<SubCategoryDto>>
        (
            data: subcategories,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }
}