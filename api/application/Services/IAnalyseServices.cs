using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;
using ecommerc_dotnet.midleware.ConfigImplment;

namespace api.application.Interface;

public class AnalyseServices(
    IUnitOfWork unitOfWork,
    IMessageService messageService
    ):IAnalyseServices
{
    public async Task<Result<AnalayesOrderDto?>> GetMonthAnalysis(Guid adminId)
    {
        User? user = await unitOfWork.UserRepository.GetUser(adminId);
        
        var isValide = user.IsValidateFunc(true);
        if (isValide is not null)
        {
            return new Result<AnalayesOrderDto?>(
                data: null,
                message: isValide.Message,
                isSuccessful: false,
                statusCode: isValide.StatusCode
            );
        }

        var  result = await unitOfWork.AnalyseRepository.GetMonthAnalysis();
        if (result is  null)
        {
            return new Result<AnalayesOrderDto?>(
                data: null,
                message: "Could not calculate analayes",
                isSuccessful: false,
                statusCode:404 
            );
        }
        
        return new Result<AnalayesOrderDto?>(
            data: result,
            message: null,
            isSuccessful: true,
            statusCode:200 
        );
    }


}