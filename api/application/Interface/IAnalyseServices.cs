using api.application.Result;
using api.Presentation.dto;

namespace api.application.Interface;

public interface IAnalyseServices
{
    Task<Result<AnalyzesOrderDto?>> GetMonthAnalysis(Guid adminId);
}