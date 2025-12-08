using System.Text;
using api.application;
using api.application.Interface;
using api.application.Services;
using api.application.UnitOfWork;
using api.domain.Interface;
using api.Infrastructure;
using api.Infrastructure.Repositories;
using api.shared.midleware;
using api.shared.signalr;
using ecommerc_dotnet.midleware.ConfigImplment;
using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.FileProviders;
using Microsoft.IdentityModel.Tokens;
using Stripe;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;

builder.Services.AddOptions();


builder.Services.AddSingleton<IConfig, ConfigurationImplement>();
builder.Services.AddTransient<IMessageService, EmailServices>();
builder.Services.AddTransient<IMessageService, NotificationServices>();

builder.Services.AddTransient<IAuthenticationService, AuthenticationServices>();






//ifile serverice
builder.Services.AddTransient<IFileServices, FileServices>();


//unitofwork
builder.Services.AddTransient<IUnitOfWork,UnitOfWork>();


//services
builder.Services.AddTransient<IUserServices, UserService>();
builder.Services.AddTransient<IStoreServices, StoreServices>();
builder.Services.AddTransient<ICategoryServices, CategoryServices>();
builder.Services.AddTransient<ISubCategoryServices, SubCategoryServices>();
builder.Services.AddTransient<IVariantServices, VariantServices>();
builder.Services.AddTransient<IBannerSerivces, BannerSerivces>();
builder.Services.AddTransient<IGeneralSettingServices, GeneralSettingServices>();
builder.Services.AddTransient<IDeliveryServices, DeliveryServices>();
builder.Services.AddTransient<IProductServices, ProductServices>();
builder.Services.AddTransient<IOrderServices, OrderServices>();
builder.Services.AddTransient<IOrderItemServices, OrderItemServices>();
builder.Services.AddTransient<IRefreshTokenServices, RefreshTokenServices>();
builder.Services.AddTransient<IAnalyseServices, AnalyseServices>();
builder.Services.AddTransient<ICurrencyServices, CurrencyServices>();



 var fireBaseConfig = Path.Combine(
     Directory.GetCurrentDirectory(), 
     "librarynotification-notification.json"
 );

 var firebaseCredential = GoogleCredential.FromFile(fireBaseConfig);
FirebaseApp.Create(new AppOptions()
{
     Credential = firebaseCredential
 });

var corsName = "AllowAllOrigins";
builder.Services.AddCors(options =>
{
    options.AddPolicy(corsName, policy =>
    {
        policy
            .WithOrigins("http://localhost:3000")
            .AllowAnyMethod() // Allows any HTTP methods (GET, POST, etc.)
            .AllowAnyHeader()
            .AllowCredentials();
    });
});

builder.Services.AddControllers();
builder.Services.AddSignalR(option =>
    option.EnableDetailedErrors = true);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Authentication
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(configuration["credentials:key"] ?? "")
            ),
            ValidIssuer = configuration["credentials:Issuer"],
            ValidAudience = configuration["credentials:Audience"]
        };
    });

// Database
var connectionUrl = configuration["ConnectionStrings:connection_url"];
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(connectionUrl));

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    app.UseSwagger();
    app.UseSwaggerUI(options => // UseSwaggerUI is called only in Development.
    {
        options.SwaggerEndpoint("/swagger/v1/swagger.json", "v1");
        options.RoutePrefix = string.Empty;
    });
}
//           AllowAllOrigins


app.UseHttpsRedirection();

app.UseStaticFiles(new StaticFileOptions
{
    FileProvider = new PhysicalFileProvider(
        Path.Combine(builder.Environment.ContentRootPath, "images")),
    RequestPath = "/StaticFiles"
});


app.UseRouting();  

app.UseCors(corsName);

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();
app.MapHub<BannerHub>("/bannerHub");
app.MapHub<OrderHub>("/orderHub"); 
app.MapHub<OrderItemHub>("/orderItemHub"); 
app.MapHub<StoreHub>("/storeHub"); 

app.ConfigureExceptionHandler();
app.Run();