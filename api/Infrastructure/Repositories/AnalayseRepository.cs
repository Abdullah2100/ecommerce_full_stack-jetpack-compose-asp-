using api.application;
using api.domain.Interface;
using api.Presentation.dto;
using Microsoft.EntityFrameworkCore;
using Npgsql;

namespace api.Infrastructure.Repositories;

public class AnalyseRepository(
    AppDbContext context
) : IAnalyseRepository
{
    public async Task<AnalyzesOrderDto?> GetMonthAnalysis()
    {
        try
        {
            using (var cmd = context.Database.GetDbConnection().CreateCommand())
            {
                cmd.CommandText = "SELECT * FROM get_monthly_stats()";
                await context.Database.OpenConnectionAsync();
                AnalyzesOrderDto? info = null;
                using (var reader = await cmd.ExecuteReaderAsync())
                {
                    if (reader.HasRows)
                    {
                        if (reader.Read())
                        {
                            info = new AnalyzesOrderDto
                            {
                                totalFee = (decimal?)reader["totalFee"],
                                totalOrders = (long?)reader["totalOrder"],
                                totalDeliveryDistance = (decimal?)reader["totalDeliveryDistance"],
                                usersCount = (long)reader["userCount"],
                                productCount = (long)reader["productcount"],
                            };
                        }
                    }
                }

                return info;
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"this error from get anaylise data {ex.Message}");
            return null;
        }
    }
}