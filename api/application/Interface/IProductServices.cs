using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface IProductServices
{
    Task<Result<List<ProductDto>>> GetProductsByStoreId(Guid storeId,int pageNum,int pageSize);
    Task<Result<List<ProductDto>>> GetProductsByCategoryId(Guid categryId,int pageNum,int pageSize);
    Task<Result<List<ProductDto>>> GetProducts(Guid storeId,Guid subCategoryId,int pageNum,int pageSize);
    Task<Result<List<ProductDto>>> GetProducts(int pageNum,int pageSize);
    Task<Result<List<AdminProductsDto>>> GetProductsForAdmin(
        Guid adminId, int pageNum,int pageSize);
    
    Task<Result<int>> GetProductsPagesForAdmin(Guid adminId,int lenght);
    Task<Result<ProductDto?>> CreateProducts(Guid userId,CreateProductDto productDto);
    Task<Result<ProductDto?>> UpdateProducts(Guid userId,UpdateProductDto productDto);
    Task<Result<bool>> DeleteProducts(Guid userId,Guid storeId,Guid id);
    
}