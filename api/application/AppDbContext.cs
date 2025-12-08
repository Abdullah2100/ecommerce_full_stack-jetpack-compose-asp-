using api.domain.entity;
using Microsoft.EntityFrameworkCore;

namespace api.application;

public class AppDbContext(DbContextOptions<AppDbContext> options) : DbContext(options)
{
    public DbSet<User> Users { get; set; }

    public DbSet<ReseatPasswordOtp> ReseatPasswords { get; set; }

    public DbSet<Address> Address { get; set; }

    public DbSet<Store> Stores { get; set; }
    public DbSet<Category> Categories { get; set; }
    public DbSet<SubCategory> SubCategories { get; set; }

    public DbSet<Banner> Banner { get; set; }

    public DbSet<Variant> Varients { get; set; }

    public DbSet<Product> Products { get; set; }
    public DbSet<ProductVariant> ProductVariants { get; set; }
    public DbSet<ProductImage> ProductImages { get; set; }
    public DbSet<Order> Orders { get; set; }
    public DbSet<OrderItem> OrderItems { get; set; }
    public DbSet<OrderProductsVariant> OrdersProductsVarients { get; set; }
    public DbSet<GeneralSetting> GeneralSettings { get; set; }

    public DbSet<Delivery> Deliveries { get; set; }
    public DbSet<Currency> Payments { get; set; }
public DbSet<Currency> Currencies { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        optionsBuilder.EnableSensitiveDataLogging();
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Currency>(cu =>
        {
            cu.HasIndex(c => new { c.Name }).IsUnique();
        });
        modelBuilder.Entity<User>(
            user =>
            {
                user.HasIndex(u => new { email = u.Email, phone = u.Phone }).IsUnique();

                user.HasMany(ca => ca.Categories)
                    .WithOne(u => u.User)
                    .HasForeignKey(c => c.OwnerId)
                    .HasPrincipalKey(u => u.Id)
                    .OnDelete(DeleteBehavior.Restrict);

                user.HasOne(u => u.Store)
                    .WithOne(st => st.user)
                    .HasForeignKey<Store>(st => st.UserId)
                    .HasPrincipalKey<User>(u => u.Id)
                    .OnDelete(DeleteBehavior.Restrict);
            }
        );

        modelBuilder.Entity<Delivery>(delev =>
        {
            delev.HasOne(de => de.User)
                .WithOne(u => u.Delivery)
                .HasForeignKey<Delivery>(de => de.UserId)
                .HasPrincipalKey<User>(u => u.Id)
                .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<Category>(ca =>
        {
            ca.HasIndex(c => new { name = c.Name }).IsUnique();

            ca.HasMany(cat => cat.SubCategories)
                .WithOne(sub => sub.Category)
                .HasForeignKey(sub => sub.CategoryId)
                .HasPrincipalKey(cat => cat.Id)
                .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<Store>(st =>
        {
            st.HasIndex(s => s.Name).IsUnique();

            st.HasMany(std => std.SubCategories)
                .WithOne(sub => sub.Store)
                .HasForeignKey(sub => sub.StoreId)
                .HasPrincipalKey(stc => stc.Id)
                .OnDelete(DeleteBehavior.Restrict);

            st.HasMany(sto => sto.Banners)
                .WithOne(bn => bn.Store)
                .HasForeignKey(ban => ban.StoreId)
                .HasPrincipalKey(sto => sto.Id)
                .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<SubCategory>(sub => { });

        modelBuilder.Entity<Banner>(ba => { });

        modelBuilder.Entity<Product>(pr =>
        {
            pr.HasMany<ProductVariant>(pro => pro.ProductVariants)
                .WithOne(p => p.Product)
                .HasForeignKey(p => p.ProductId)
                .HasPrincipalKey(pro => pro.Id);
            pr.HasOne<SubCategory>(pro => pro.SubCategory)
                .WithMany(sub => sub.Products)
                .HasForeignKey(pro => pro.SubcategoryId)
                .HasPrincipalKey(sub => sub.Id)
                .OnDelete(DeleteBehavior.Restrict);
            pr.HasOne(pro => pro.Store)
                .WithMany(st => st.Products)
                .HasForeignKey(pro => pro.StoreId)
                .HasPrincipalKey(str => str.Id);
            pr.HasMany<ProductImage>(pro => pro.ProductImages)
                .WithOne(pim => pim.Product)
                .HasForeignKey(pim => pim.ProductId)
                .HasPrincipalKey(pro => pro.Id);
            
        });

        modelBuilder.Entity<Variant>(va =>
        {
            va.HasIndex(var => var.Name).IsUnique();
            va.HasMany(var => var.ProductVariants)
                .WithOne(var => var.Variant)
                .HasForeignKey(var => var.VariantId)
                .HasPrincipalKey(var => var.Id)
                .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<Order>(or =>
        {
            or.HasOne<User>(ord => ord.User)
                .WithMany(u => u.Orders)
                .HasForeignKey(ord => ord.UserId)
                .HasPrincipalKey(u => u.Id);

            or.HasMany(ord => ord.Items)
                .WithOne(orIt => orIt.Order)
                .HasForeignKey(ord => ord.OrderId)
                .HasPrincipalKey(orIt => orIt.Id);
            
             
        })
        ;

        modelBuilder.Entity<OrderItem>(oIt =>
        {
            oIt.HasOne(orIt => orIt.Store)
                .WithMany(st => st.OddrderItems)
                .HasForeignKey(orIt => orIt.StoreId)
                .HasPrincipalKey(st => st.Id);

            oIt.HasMany(orIt => orIt.OrderProductsVariants)
                .WithOne(opv => opv.OrderItem)
                .HasForeignKey(orIt => orIt.OrderItemId)
                .HasPrincipalKey(orIt => orIt.Id);
        });

        modelBuilder.Entity<OrderProductsVariant>(opv =>
        {
            opv.HasOne(OPV => OPV.ProductVariant)
                .WithMany(or => or.OrderProductsVariants)
                .HasForeignKey(or => or.ProductVariantId)
                .HasPrincipalKey(or => or.Id);
        });

      
        base.OnModelCreating(modelBuilder);
    }
    
}