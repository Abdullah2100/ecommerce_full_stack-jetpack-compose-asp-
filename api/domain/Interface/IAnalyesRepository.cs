using api.Presentation.dto;

namespace api.domain.Interface;

public interface IAnalyseRepository
{
    Task<AnalyzesOrderDto?> GetMonthAnalysis();
}