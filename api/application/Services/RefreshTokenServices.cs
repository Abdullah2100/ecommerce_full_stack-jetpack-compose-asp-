using System.Security.Claims;
using api.application.Interface;
using api.application.Result;
using api.domain.entity;
using api.Infrastructure;
using api.Presentation.dto;
using api.shared.mapper;

namespace api.application.Services;

public class RefreshTokenServices(
    IUnitOfWork unitOfWork,
    IAuthenticationService authenticationService) : IRefreshTokenServices
{
    public bool IsRefreshToken(string issueAt, string expireAt)
    {
        long lIssueDate = long.Parse(issueAt);
        long lExpireDate = long.Parse(expireAt);

        var issueDateTime = DateTimeOffset.FromUnixTimeSeconds(lIssueDate).DateTime;
        var expireTime = DateTimeOffset.FromUnixTimeSeconds(lExpireDate).DateTime;

        var result = issueDateTime - expireTime;
        return result.Days >= 29;
    }

    public async Task<Result<AuthDto?>> GenerateRefreshToken(
        string token,
        Claim? id,
        Claim? issuAt,
        Claim? expireAt)
    {
        if (id is null || issuAt is null || expireAt is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "error while adding delivery",
                isSuccessful: false,
                statusCode: 400
            );
        }

        var idHolder = Guid.Parse(id.Value);
        User? user = await unitOfWork.UserRepository
            .GetUser(idHolder);
        
        Delivery? delivery = await unitOfWork.DeliveryRepository.GetDelivery(idHolder);

        var validation = user.IsValidateFunc(false);
        if (validation is not null && delivery is null)
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: validation.Message,
                isSuccessful: false,
                statusCode: validation.StatusCode
            );
        }

        if (!IsRefreshToken(issuAt.Value, expireAt.Value))
        {
            return new Result<AuthDto?>
            (
                data: null,
                message: "send valid token ",
                isSuccessful: false,
                statusCode: 400
            );
        }

        var tokenHolder = authenticationService.GenerateToken(
            id: idHolder,
            email: (user?.Email??delivery?.User?.Email) ?? string.Empty);

        var refreshTokenHolder = authenticationService.GenerateToken(
            id: idHolder,
            email: user?.Email?? (delivery?.User?.Email) ?? string.Empty,
            EnTokenMode.RefreshToken);

        return new Result<AuthDto?>(
            isSuccessful: true,
            data: new AuthDto { RefreshToken = refreshTokenHolder, Token = tokenHolder },
            message: "",
            statusCode: 200
        );
    }
}