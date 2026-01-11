using api.application;
using api.domain.entity;
using api.domain.Interface;
using Microsoft.EntityFrameworkCore;

namespace api.Infrastructure.Repositories;

public class UserRepository(AppDbContext dbContext) : IUserRepository
{
    public async Task<User?> GetUser(Guid id)
    {
        User? user = await dbContext
            .Users
            .Include(u => u.Store)
            .AsNoTracking()
            .FirstOrDefaultAsync(u => u.Id == id);
        if (user == null) return null;

        user.Addresses = await dbContext
            .Address
            .AsNoTracking()
            .Where(u => u.OwnerId == id)
            .ToListAsync();

        return user;
    }

    public async Task<User?> GetUser(string email)
    {
        User? user = await dbContext
            .Users
            .Include(u => u.Store)
            .AsNoTracking()
            .FirstOrDefaultAsync(u =>  u.Email == email);
        if (user == null) return null;

        user.Addresses = await dbContext
            .Address
            .AsNoTracking()
            .Where(u => u.OwnerId == user.Id)
            .ToListAsync();

        return user;
    }

    public async Task<int> GetUserCount()
    {
        return await dbContext
            .Users
            .AsNoTracking()
            .CountAsync();
    }

    public async Task<int> GetUserAddressCount(Guid id)
    {
        return await dbContext
            .Address
            .AsNoTracking()
            .Where(u => u.OwnerId == id)
            .CountAsync();
    }

    public async Task<User?> GetUserByStoreId(Guid id)
    {
        User? user = await dbContext
            .Users
            .Include(u => u.Store)
            .AsSplitQuery()
            .AsNoTracking()
            .FirstOrDefaultAsync(u => u.Store != null && u.Store.Id == id);
        if (user == null) return null;

        user.Addresses = await dbContext
            .Address
            .AsNoTracking()
            .Where(u => u.OwnerId == id)
            .ToListAsync();

        return user;
    }

    public async Task<List<User>> GetUsers(int page, int length)
    {
        List<User>? users = await dbContext
            .Users
            .Include(u => u.Store)
            .AsSplitQuery()
            .AsNoTracking()
            .Skip((page - 1) * length)
            .OrderDescending()
            .Take(length)
            .ToListAsync();

        foreach (var user in users)
        {
            user.Addresses = await dbContext
                .Address
                .AsNoTracking()
                .Where(u => u.OwnerId == user.Id)
                .ToListAsync();
        }

        return users; 
    }

    public async Task<User?> GetUser(string username, string password)
    {
        try
        {
            User? user = await dbContext
                .Users
                .Include(u => u.Store)
                .AsNoTracking()
                .FirstOrDefaultAsync(u => (u.Name == username || u.Email == username) && u.Password == password);

            if (user == null) return null;

            user.Addresses = await dbContext
                .Address
                .AsNoTracking()
                .Where(u => u.OwnerId == user.Id)
                .ToListAsync();

            return user;
        }
        catch (Exception ex)
        {
            Console.WriteLine($"this the excptino error from get user {ex.Message}");
            return null;
        }
    }

    public async Task<bool> IsExist(int role)
    {
        return await dbContext
            .Users
            .AsNoTracking()
            .AnyAsync(u => u.Role == role);
    }

    public async Task<bool> IsExistByPhone(string phone)
    {
        return (await dbContext
                .Users
                .AsNoTracking()
                .AnyAsync(u => u.Phone == phone)
            );
    }

    public async Task<bool> IsExistByEmail(string email)
    {
        return (await dbContext
                .Users
                .AsNoTracking()
                .AnyAsync(u => u.Email == email)
            );
    }

  
    public void  Add(User entity)
    {
         dbContext.Users.Add(entity);
    }

    public void  Update(User entity)
    {
         dbContext.Users.Update(entity);
    }

    public void  Delete(Guid id)
    {
        User? user =  dbContext.Users.Find(id);
        if (user == null) throw new ArgumentNullException();
        user.IsBlocked = true;
    }

    public async Task<bool> IsExist(Guid id)
    {
        return await dbContext.Users.FindAsync(id) != null;
    }
}