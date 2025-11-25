using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.extentions;
using api.util;
using ecommerc_dotnet.midleware.ConfigImplment;

namespace api.application.Services;

public class ProductServices(
    IConfig config,
    IUnitOfWork unitOfWork,
    IFileServices fileServices
)
    : IProductServices
{
    public async Task<Result<List<ProductDto>>> GetProductsByStoreId(
        Guid storeId,
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await unitOfWork.ProductRepository
                .GetProducts(storeId, pageNum, pageSize))
            .Select((de) => de.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<ProductDto>>> GetProductsByCategoryId(
        Guid categryId,
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await unitOfWork.ProductRepository
                .GetProductsByCategory(categryId, pageNum, pageSize))
            .Select((de) => de.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<ProductDto>>> GetProducts(
        Guid storeId,
        Guid subCategoryId,
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await unitOfWork.ProductRepository
                .GetProducts(storeId, subCategoryId, pageNum, pageSize))
            .Select((de) => de.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<ProductDto>>> GetProducts(
        int pageNum,
        int pageSize
    )
    {
        List<ProductDto> products = (await unitOfWork.ProductRepository
                .GetProducts(pageNum, pageSize))
            .Select((de) => de.ToDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<ProductDto>>(
            data: products,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<List<AdminProductsDto>>> GetProductsForAdmin(
        Guid adminId,
        int pageNum,
        int pageSize
    )
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(adminId);
        if (user is null)
        {
            return new Result<List<AdminProductsDto>>
            (
                data: new List<AdminProductsDto>(),
                message: "user not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (user.Role != 0)
        {
            return new Result<List<AdminProductsDto>>
            (
                data: new List<AdminProductsDto>(),
                message: "not authorized user",
                isSuccessful: false,
                statusCode: 400
            );
        }


        List<AdminProductsDto> products = (await unitOfWork.ProductRepository
                .GetProducts(pageNum, pageSize))
            .Select((de) => de.ToAdminDto(config.getKey("url_file")))
            .ToList();

        return new Result<List<AdminProductsDto>>(
            data: products,
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }
/*
    private async Task<Result<ProductDto?>?> isUserNotExistOrNotHasStore(Guid userId)
    {
        User? user = await unitOfWork.UserRepository
            .GetUser(userId);
        if (user is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "user not found",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if (user.IsBlocked)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "user is blocked",
                isSuccessful: false,
                statusCode: 400
            );
        }

        if (user.Store is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "user not has store",
                isSuccessful: false,
                statusCode: 400
            );
        }

        if (user.Store.IsBlock)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "admin is block you store from creating product",
                isSuccessful: false,
                statusCode: 400
            );
        }

        return null;
    }
*/
    public async Task<Result<ProductDto?>> CreateProducts(
        Guid userId,
        CreateProductDto productDto
    )
    {

        User? user = await unitOfWork.UserRepository.GetUser(userId);

        var isValidate = user.IsValidateFunc(false, true);
       
        if (isValidate is not null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: isValidate.Message,
                isSuccessful: false,
                statusCode: isValidate.StatusCode 
            );
        }


        string? savedThumbnail = await fileServices.SaveFile(
            productDto.Thumbnail,
            EnImageType.Product);
        List<string>? savedImage = await fileServices.SaveFile(
            productDto.Images,
            EnImageType.Product);
        if (savedImage is null || savedThumbnail is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "error while saving image ",
                isSuccessful: false,
                statusCode: 400
            );
        }


        var id = ClsUtil.GenerateGuid();

        List<ProductImage> images = savedImage.Select(pi => new ProductImage
            {
                Id = ClsUtil.GenerateGuid(),
                Path = pi,
                ProductId = id
            })
            .ToList();

        if ((images.Count) > 20)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product image can maximum has 20 images",
                isSuccessful: false,
                statusCode: 404
            );
        }


        List<ProductVariant>? productVariants = null;
        if (productDto.ProductVariants is not null)
            productVariants = productDto
                .ProductVariants!.Select(pv =>
                    new ProductVariant
                    {
                        Id = ClsUtil.GenerateGuid(),
                        Name = pv.Name,
                        Percentage = pv.Percentage,
                        ProductId = id,
                        VariantId = pv.VariantId,
                        OrderProductsVariants = null
                    })
                .ToList();

        if (productVariants is not null && productVariants.Count > 20)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "productvarient  can maximum has 20 images",
                isSuccessful: false,
                statusCode: 404
            );
        }

        var product = new Product
        {
            Id = id,
            Name = productDto.Name,
            Description = productDto.Description,
            SubcategoryId = productDto.SubcategoryId,
            StoreId = productDto.StoreId,
            Price = productDto.Price,
            CreatedAt = DateTime.Now,
            UpdatedAt = null,
            Thumbnail = savedThumbnail,
        };

        unitOfWork.ProductRepository.Add(product);
        unitOfWork.ProductImageRepository.AddProductImage(images);
        if (productVariants is not null)
            unitOfWork.ProductVariantRepository.AddProductVariants(productVariants);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "error while adding product",
                isSuccessful: false,
                statusCode: 400
            );
        }

        product = await unitOfWork.ProductRepository.GetProduct(product.Id);

        return new Result<ProductDto?>
        (
            data: product?.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 201
        );
    }

    public async Task<Result<ProductDto?>> UpdateProducts(
        Guid userId, UpdateProductDto productDto
    )
    {
        if (productDto.IsEmpty())
            return new Result<ProductDto?>
            (
                data: null,
                message: "",
                isSuccessful: true,
                statusCode: 201
            );

        User? user = await unitOfWork.UserRepository.GetUser(userId);

        var isValidate = user.IsValidateFunc(false, true);
       
        if (isValidate is not null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: isValidate.Message,
                isSuccessful: false,
                statusCode: isValidate.StatusCode 
            );
        }
        
        if (productDto.SubcategoryId is not null &&
            !(await unitOfWork.SubCategoryRepository.IsExist((Guid)productDto!.SubcategoryId!)))
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "subCategory  is not found ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        Product? product = await unitOfWork.ProductRepository.GetProduct(productDto.Id, productDto.StoreId);

        if (product is null)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product is not found ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        int result = 0;

        //delete preview images
        if (productDto.Deletedimages is not null)
        {
              unitOfWork.ProductImageRepository.DeleteProductImages(productDto.Deletedimages,
                productDto.Id);

            fileServices.DeleteFile(productDto.Deletedimages);
        }

        //delete preview productvarients
        if (productDto.DeletedProductVariants is not null)
        {
             unitOfWork.ProductVariantRepository.DeleteProductVariant(productDto.DeletedProductVariants,
                productDto.Id);
            
        }

        string? savedThumbnail = null;
        List<ProductImage>? savedImage = null;

        if (productDto.Thumbnail is not null)
            savedThumbnail = await fileServices.SaveFile(
                productDto.Thumbnail,
                EnImageType.Product);

        if (productDto.Images is not null)
            savedImage = (await fileServices.SaveFile(
                    productDto.Images,
                    EnImageType.Product)
                )
                ?.Select(im => new ProductImage
                {
                    Id = ClsUtil.GenerateGuid(),
                    Path = im,
                    ProductId = product.Id
                }).ToList();

        if (savedImage is not null && (savedImage.Count + product?.ProductImages?.Count) > 20)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product image can maximum has 20 images",
                isSuccessful: false,
                statusCode: 404
            );
        }

        if ((savedImage?.Count + product?.ProductImages?.Count) < 1)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product image must  has 2 image at least ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        List<ProductVariant>? productVariants = null;
        if (productDto.ProductVariants is not null)
            productVariants = productDto
                .ProductVariants!.Select(pv =>
                    new ProductVariant
                    {
                        Id = ClsUtil.GenerateGuid(),
                        Name = pv.Name,
                        Percentage = pv.Percentage,
                        ProductId = product!.Id,
                        VariantId = pv.VariantId,
                        OrderProductsVariants = null
                    })
                .ToList();
        
        if (productVariants is not null && (productVariants.Count + product?.ProductVariants?.Count) > 20)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "product image can maximum has 20 images",
                isSuccessful: false,
                statusCode: 404
            );
        }

        //delete the previs images 


        product!.Name = productDto.Name ?? product.Name;
        product.Description = productDto.Description ?? product.Description;
        product.SubcategoryId = productDto.SubcategoryId ?? product.SubcategoryId;
        product.Price = productDto.Price ?? product.Price;
        product.UpdatedAt = DateTime.Now;
        product.Thumbnail = savedThumbnail ?? product.Thumbnail;
        product.ProductVariants = productVariants;
        product.ProductImages = savedImage;

        unitOfWork.ProductRepository.Update(product);
        result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<ProductDto?>
            (
                data: null,
                message: "error while updating product",
                isSuccessful: false,
                statusCode: 400
            );
        }

        Product? finalUpdateProduct = await unitOfWork.ProductRepository.GetProduct(product.Id);

        return new Result<ProductDto?>
        (
            data: finalUpdateProduct?.ToDto(config.getKey("url_file")),
            message: "",
            isSuccessful: true,
            statusCode: 200
        );
    }

    public async Task<Result<bool>> DeleteProducts(
        Guid userId,
        Guid id
    )
    {
        User? user = await unitOfWork.UserRepository.GetUser(userId);

        var isValidate = user.IsValidateFunc(false, true);
       
        if (isValidate is not null)
        {
            return new Result<bool>
            (
                data: false,
                message: isValidate.Message,
                isSuccessful: false,
                statusCode: isValidate.StatusCode 
            );
        }

        Product? product = await unitOfWork.ProductRepository.GetProduct(id, user.Store.Id);

        if (product is null || id != product.Id)
        {
            return new Result<bool>
            (
                data: false,
                message: "product is not found ",
                isSuccessful: false,
                statusCode: 404
            );
        }

        unitOfWork.ProductRepository.Delete(product.Id);
        int result = await unitOfWork.SaveChanges();

        if (result == 0)
        {
            return new Result<bool>
            (
                data: false,
                message: "product had linke with some order",
                isSuccessful: false,
                statusCode: 400
            );
        }

        if (product.ProductImages is not null)
            foreach (var image in product.ProductImages)
            {
                fileServices.DeleteFile(image.Path);
            }

        return new Result<bool>
        (
            data: true,
            message: "",
            isSuccessful: true,
            statusCode: 204
        );
    }
}