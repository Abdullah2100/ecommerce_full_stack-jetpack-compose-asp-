using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;
using api.util;
using ecommerc_dotnet.midleware.ConfigImplment;

namespace api.application.Services;

public class CategoryServices(
    IWebHostEnvironment host,
    IConfig config,
    IUnitOfWork unitOfWork,
    IFileServices fileServicee)
    : ICategoryServices
{
    public async Task<Result<CategoryDto?>> CreateCategory(CreateCategoryDto categoryDto, Guid adminId)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);

        var isValid = user.IsValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        if (await unitOfWork.CategoryRepository.IsExist(categoryDto.Name))
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "there are category with the same name",
                isSuccessful: false,
                statusCode: 400
            );
        }

        string? imagePath = await fileServicee
            .SaveFile(categoryDto.Image,
                EnImageType.Category);

        if (imagePath is null)
            return new Result<CategoryDto?>
            (
                data: null,
                message: "there error while saving image to server",
                isSuccessful: false,
                statusCode: 400
            );
        Guid categoryId = ClsUtil.GenerateGuid();

        Category category = new Category
        {
            Id = categoryId,
            Name = categoryDto.Name,
            Image = imagePath,
            IsBlocked = false,
            OwnerId = user!.Id
        };
        unitOfWork.CategoryRepository.Add(category);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "error while adding category",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<CategoryDto?>
        (
            data: category?.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<CategoryDto?>> UpdateCategory(UpdateCategoryDto categoryDto, Guid adminId)
    {
        if (categoryDto.IsEmpty())
            return new Result<CategoryDto?>
            (
                data: null,
                message: "no change found",
                isSuccessful: false,
                statusCode: 200
            );

        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);

        var isValid = user.IsValidateFunc(true);
        if (isValid is not null)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: isValid.Message,
                isSuccessful: false,
                statusCode: isValid.StatusCode
            );
        }

        if (categoryDto.Name is not null)
            if (await unitOfWork.CategoryRepository.IsExist(categoryDto.Name, categoryDto.Id))
            {
                return new Result<CategoryDto?>
                (
                    data: null,
                    message: "there are category with the same name",
                    isSuccessful: false,
                    statusCode: 400
                );
            }

        Category? category = await unitOfWork.CategoryRepository.GetCategory(categoryDto.Id);


        if (category is null)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "category not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        string? image = null;

        if (categoryDto?.Image is not null)
        {
            if (categoryDto?.Image is not null)
                fileServicee.DeleteFile(category.Image);
            image = await fileServicee 
                .SaveFile(categoryDto!.Image!,
                    EnImageType.Category);
        }

        category.Name = categoryDto?.Name ?? category.Name;
        category.Image = image ?? category.Image;
        category.UpdatedAt = DateTime.Now;

        unitOfWork.CategoryRepository.Update(category);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<CategoryDto?>
            (
                data: null,
                message: "error while update category",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return new Result<CategoryDto?>
        (
            data: category.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> DeleteCategory(Guid categoryId, Guid adminId)
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

        if (!(await unitOfWork.CategoryRepository.IsExist(categoryId)))
        {
            return new Result<bool>
            (
                data: false,
                message: "category not found",
                isSuccessful: false,
                statusCode: 400
            );
        }

        unitOfWork.CategoryRepository.Delete(categoryId);
        int result = await unitOfWork.SaveChanges();
        
        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "error while delete category",
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

    public async Task<Result<List<CategoryDto>>> GetCategories(int pageNumber, int pageSize)
    {
        List<CategoryDto> categories = (await unitOfWork.CategoryRepository.GetCategories(pageNumber, pageSize))
            .Select(ca => ca.ToDto(config.getKey("url_file")))
            .ToList();
        return new Result<List<CategoryDto>>
        (
            data: categories,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }
}