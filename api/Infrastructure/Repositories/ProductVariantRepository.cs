using api.application;
using api.domain.entity;
using api.domain.Interface;
using api.Presentation.dto;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class ProductVariantRepository(AppDbContext context) : IProductVariantRepository
{
    public async Task<ProductVariant?> GetProductVarient(Guid productId, Guid id)
    {
        return await context.ProductVariants
            .FirstOrDefaultAsync(or => or.ProductId == productId && or.Id == id);
    }

    public async Task SaveProductVariants(ICollection<ProductVariant> productVariants)
    {
        for (var i = 0; i < productVariants.Count; i++)
        {
            if (productVariants.ElementAt(i)?.Id is not null)
                await Task.Run(() => Update(productVariants.ElementAt(i)));
            else
                await Task.Run(() => Add(productVariants.ElementAt(i)));
        }
    }


    public void DeleteProductVariantByProductId(Guid productId)
    {
        var result = context.ProductVariants.Where(p => p.ProductId == productId).ToList();
        context.ProductVariants.RemoveRange(result);
    }

    public void DeleteProductVariant(List<CreateProductVariantDto> productVariants, Guid productId)
    {
        try
        {
            for (var i = 0; i < productVariants.Count; i++)
            {
                var result = context.ProductVariants
                    .FirstOrDefault(pv =>
                        pv.ProductId == productId && pv.VariantId == productVariants[i].VariantId &&
                        pv.Name == productVariants[i].Name
                    );
                if (result is not null)
                    context.ProductVariants.Remove(result);
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"{ex.Message}");
        }
    }

    public void Add(ProductVariant entity)
    {
        context.ProductVariants.Add(entity);
    }

    public void Update(ProductVariant entity)
    {
        context.ProductVariants.Update(entity);
    }
}