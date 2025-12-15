using api.application;
using api.domain.entity;
using api.domain.Interface;
using api.Presentation.dto;
using Microsoft.EntityFrameworkCore;
using Npgsql;

namespace api.Infrastructure.Repositories;

public class OrderRepository(AppDbContext context)
    : IOrderRepository
{
    public async Task<IEnumerable<Order>> GetOrders(
        Guid userId,
        int pageNum,
        int pageSize
    )
    {
        var orders = await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .Where(o => o.UserId == userId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
        foreach (var order in orders)
        {
            order.Items = await context.OrderItems
                .Include(oi => oi.Order)
                .Include(oi => oi.Product)
                .Include(oi => oi.Store)
                .AsSplitQuery()
                .Where(oi => oi.OrderId == order.Id)
                .ToListAsync();
        }

        return orders;
    }

    public async Task<IEnumerable<Order>> GetOrders(int page, int length)
    {
        var orders = await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .Skip((page - 1) * length)
            .Take(length)
            .OrderDescending()
            .ToListAsync();
        foreach (var order in orders)
        {
            order.Items = await context.OrderItems
                .Include(oi => oi.Order)
                .Include(oi => oi.Product)
                .Include(oi => oi.Store)
                .AsSplitQuery()
                .Where(oi => oi.OrderId == order.Id)
                .Select(it => new OrderItem
                {
                    Id = it.Id,
                    OrderId = it.OrderId,
                    ProductId = it.ProductId,
                    Price = it.Price,
                    Quantity = it.Quantity,
                    StoreId = it.StoreId,
                    Order = it.Order,
                    Store = new Store
                    {
                        Id = it.Store.Id,
                        Name = it.Store.Name,
                        WallpaperImage = "",
                        SmallImage = "",
                        IsBlock = it.Store.IsBlock,
                        UserId = it.Store.UserId,
                        Addresses = context
                            .Address
                            .AsNoTracking()
                            .Where(ad => ad.OwnerId == it.Store.Id)
                            .ToList()
                    },
                    Product = it.Product,
                    OrderProductsVariants = it.OrderProductsVariants,
                    Status = it.Status
                })
                .ToListAsync();
        }

        return orders;
    }

    public async Task<Order?> GetOrder(Guid id)
    {
        var order = await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(o => o.Id == id);
        if (order is null) return null;
        order.Items = await context.OrderItems
            .Include(oi => oi.Order)
            .Include(oi => oi.Product)
            .Include(oi => oi.Store)
            .AsSplitQuery()
            .AsNoTracking()
            .Where(oi => oi.OrderId == order.Id)
            .ToListAsync();
        return order;
    }


    public async Task<Order?> GetOrder(Guid id, Guid userId)
    {
        var order = await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(o => o.Id == id && o.UserId == userId);
        if (order is null) return null;
        order.Items = await context.OrderItems
            .Include(oi => oi.Order)
            .Include(oi => oi.Product)
            .Include(oi => oi.Store)
            .AsSplitQuery()
            .Where(oi => oi.OrderId == order.Id)
            .ToListAsync();

        return order;
    }

    public async Task<bool> IsExist(Guid id)
    {
        return await context.Orders
            .AsNoTracking()
            .AnyAsync(o => o.Id == id);
    }

    public async Task<bool> IsCanCancelOrder(Guid id)
    {
        return await context
            .OrderItems
            .AsNoTracking()
            .AnyAsync(i => i.OrderId == id && i.Status == enOrderItemStatus.ReceivedByDelivery
            );
    }

    public async Task<bool> IsValidTotalPrice(decimal totalPrice, List<CreateOrderItemDto> items,string symbol)
    {
        bool isAmbiguous = false;
        decimal realPrice = 0;

        foreach (var item in items)
        {
            var product = await context.Products.FindAsync(item.ProductId);
            var currencies = await context.Currencies.ToListAsync();
            decimal varientPrice = 1;
            //itrate throw every productvarientid
            for (var i = 0; i < item.ProductVariant?.Count; i++)
            {
                //query to get the product variant
                var productVariantPrice =
                    await context.ProductVariants.FirstOrDefaultAsync(product =>
                        product.ProductId == product.Id && product.Id == item.ProductVariant[i]);
                
                if (productVariantPrice is null)
                {
                    isAmbiguous = true;
                    break;
                }

                varientPrice = varientPrice * productVariantPrice.Percentage;
            };

            if (isAmbiguous == true)
            {
                break;
            }

            if (product?.Price != item.Price)
            {
                isAmbiguous = true;
                break;
            }

            realPrice += ConvertPriceFromCurrencyToAnother(((varientPrice * product.Price) * item.Quantity),product.Symbol,symbol,currencies);
        }


        if (isAmbiguous)
        {
            return false;
        }

        return realPrice == totalPrice;
    }

    public async Task<IEnumerable<Order>> GetOrderNoBelongToAnyDelivery(int pageNum, int pageSize)
    {
        var orders =
            await context.Orders
                .Include(o => o.Items)
                .Include(o => o.User)
                .AsSplitQuery()
                .AsNoTracking()
                .Where(o => o.DeliveryId == null)
                .Skip((pageNum - 1) * pageSize)
                .Take(pageSize)
                .OrderDescending()
                .ToListAsync();
        foreach (var order in orders)
        {
            order.Items = await context.OrderItems
                .Include(it => it.Order)
                .Include(it => it.OrderProductsVariants)
                .Include(oi => oi.Product)
                .Include(oi => oi.Store)
                .AsSplitQuery()
                .Where(oi => oi.OrderId == order.Id)
                .Select(it => new OrderItem
                {
                    Id = it.Id,
                    OrderId = it.OrderId,
                    ProductId = it.ProductId,
                    Price = it.Price,
                    Quantity = it.Quantity,
                    StoreId = it.StoreId,
                    Order = it.Order,
                    Store = new Store
                    {
                        Id = it.Store.Id,
                        Name = it.Store.Name,
                        WallpaperImage = "",
                        SmallImage = "",
                        IsBlock = it.Store.IsBlock,
                        UserId = it.Store.UserId,
                        Addresses = context
                            .Address
                            .AsNoTracking()
                            .Where(ad => ad.OwnerId == it.Store.Id)
                            .ToList()
                    },
                    Product = it.Product,
                    OrderProductsVariants = it.OrderProductsVariants,
                    Status = it.Status
                })
                .ToListAsync();
        }


        return orders;
    }

    public async Task<IEnumerable<Order>> GetOrderBelongToDelivery(Guid deliveryId, int pageNum, int pageSize)
    {
        var orders = await context.Orders
            .Include(o => o.User)
            .Include(o => o.Items)
            .AsSplitQuery()
            .AsNoTracking()
            .Where(o => o.DeliveryId == deliveryId)
            .Skip((pageNum - 1) * pageSize)
            .Take(pageSize)
            .OrderDescending()
            .ToListAsync();
        foreach (var order in orders)
        {
            order.Items = await context.OrderItems
                .Include(oi => oi.Order)
                .Include(oi => oi.Product)
                .Include(oi => oi.Store)
                .AsSplitQuery()
                .Where(oi => oi.OrderId == order.Id)
                .Select(it => new OrderItem
                {
                    Id = it.Id,
                    OrderId = it.OrderId,
                    ProductId = it.ProductId,
                    Price = it.Price,
                    Quantity = it.Quantity,
                    StoreId = it.StoreId,
                    Order = it.Order,
                    Store = new Store
                    {
                        Id = it.Store.Id,
                        Name = it.Store.Name,
                        WallpaperImage = "",
                        SmallImage = "",
                        IsBlock = it.Store.IsBlock,
                        UserId = it.Store.UserId,
                        Addresses = context
                            .Address
                            .AsNoTracking()
                            .Where(ad => ad.OwnerId == it.Store.Id)
                            .ToList()
                    },
                    Product = it.Product,
                    OrderProductsVariants = it.OrderProductsVariants,
                    Status = it.Status
                })
                .ToListAsync();
        }

        return orders;
    }

    public void RemoveOrderFromDelivery(Guid id, Guid deliveryId)
    {
        Order? result = context
            .Orders
            .FirstOrDefault(o => o.Id == id && o.DeliveryId == deliveryId);

        if (result == null) throw new ArgumentNullException();
        result.DeliveryId = null;
    }

    public decimal ConvertPriceFromCurrencyToAnother(decimal price, string productSymbol,string currentSymbol, List<Currency> currencies)
    {

            var  currentCurrency = currencies.First(x => x.Symbol == currentSymbol);
            var  productCurrency = currencies.First(x => x.Symbol == productSymbol);

            switch (currentCurrency.IsDefault && !productCurrency.IsDefault) {
                case true:
                    return price / (productCurrency.Value);
                default:
                {
                    switch (currentCurrency== productCurrency) {
                        case true: return price;
                        default:
                        {
                           return (price/productCurrency.Value)* currentCurrency.Value;
                        }
                    }
                }
            }

    }

    public void Add(Order entity)
    {
        context.Orders.Add(entity);
    }

    public void Update(Order entity)
    {
        context.Orders.Update(entity);
    }

    public void Delete(Guid id)
    {
        var orders = context.Orders.Where(o => o.Id == id).ToList();
        context.Orders.RemoveRange(orders);
    }



    public async Task<bool> IsSavedDistanceToOrder(Guid id)
    {
        var result = (await isSavedDistance(id) == true ? 1 : 0);
        if (result == 0)
        {
            Delete(id);
            return false;
        }

        return true;
    }

    private async Task<bool> isSavedDistance(Guid orderId)
    {
        try
        {
            using (var command = context
                       .Database
                       .GetDbConnection()
                       .CreateCommand())
            {
                command.CommandText = "SELECT * FROM fun_calculate_distance_between_user_and_stores(@orderId)";
                command.Parameters.Add(new NpgsqlParameter("@orderId", orderId));
                await context.Database.OpenConnectionAsync();
                var result = await command.ExecuteScalarAsync();
                return (bool?)result == true ? true : false;
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine("Error from isSavedDistance " + ex);
            return false;
        }
    }
}