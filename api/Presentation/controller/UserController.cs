using System.Security.Claims;
using api.application.Interface;
using api.Presentation.dto;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;

namespace api.Presentation.controller;

[Authorize]
[ApiController]
[Route("api/User")]
public class UserController(
    IUserServices userServices,
    IAuthenticationService authenticationService
    ) : ControllerBase
{
    [AllowAnonymous]
    [HttpPost("signup")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> SignUp([FromBody] SignupDto data)
    {
        var result = await userServices.Signup(data);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }
    


    [AllowAnonymous]
    [HttpPost("login")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Login([FromBody] LoginDto data)
    {
        var result = await userServices.Login(data);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpGet("me")]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> GetUser()
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));
        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.GetMe(userId.Value);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpGet("{page:int}")]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> GetUsers(int page)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? adminId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            adminId = outId;
        }

        if (adminId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.GetUsers(page, adminId.Value);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }
    //this to get user per pages like we hav 20 pages of user 25 user at one per page 
    
    [HttpGet("pages")]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> GetUserPages()
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? adminId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            adminId = outId;
        }

        if (adminId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.GetUsersPages( adminId.Value);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpDelete("status/{userId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> BlockOrUnBlockUser(Guid userId)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? adminId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            adminId = outId;
        }

        if (adminId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.BlockOrUnBlockUser(adminId.Value, userId);

        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }



    [HttpPut("")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> UpdateUser(
        [FromForm] UpdateUserInfoDto userData
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value.ToString(), out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.UpdateUser(userData, userId.Value);

        return result.IsSuccessful switch
        {
            true => StatusCode(result
                .StatusCode, result.Data),
            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpPost("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public async Task<IActionResult> AddNewUserAddress(
        [FromBody] CreateAddressDto address
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.AddAddressToUser(address, userId.Value);

        return result.IsSuccessful switch
        {
            true => StatusCode(result
                .StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }

    [HttpPut("address")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> UpdateUserLocation(
        [FromBody] UpdateAddressDto address
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.UpdateUserAddress(address, userId.Value);


        return result.IsSuccessful switch
        {
            true => StatusCode(result
                .StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }

    [HttpDelete("address/{addressId}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> DeleteUserLocation(
        Guid addressId
    )
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? adminId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            adminId = outId;
        }

        if (adminId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.DeleteUserAddress(addressId, adminId.Value);


        return result.IsSuccessful switch
        {
            true => StatusCode(result
                .StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [HttpPatch("address/active/{addressId:guid}")]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> UpdateUserCurrentLocation(Guid addressId)
    {
        StringValues authorizationHeader = HttpContext.Request.Headers["Authorization"];
        Claim? id = authenticationService.GetPayloadFromToken("id",
            authorizationHeader.ToString().Replace("Bearer ", ""));

        Guid? userId = null;
        if (Guid.TryParse(id?.Value, out Guid outId))
        {
            userId = outId;
        }

        if (userId is null)
        {
            return Unauthorized("هناك مشكلة في التحقق");
        }

        var result = await userServices.UpdateUserCurrentAddress(addressId, userId.Value);


        return result.IsSuccessful switch
        {
            true => StatusCode(result
                .StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }




    [AllowAnonymous]
    [HttpPost("generateOtp")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> GenerateOtp(
        [FromBody] ForgetPasswordDto otp
    )
    {
        var result = await userServices.GenerateOtp(otp);


        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [AllowAnonymous]
    [HttpPost("otpVerification")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    public async Task<IActionResult> VerifingOtp(
        [FromBody] CreateVerificationDto verification
    )
    {
        var result = await userServices.OtpVerification(verification);


        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }


    [AllowAnonymous]
    [HttpPost("reseatPassword")]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public async Task<IActionResult> ReseatPassword([FromBody] CreateReseatePasswordDto data)
    {
        var result = await userServices.ReseatePassword(data);


        return result.IsSuccessful switch
        {
            true => StatusCode(result.StatusCode, result.Data),

            _ => StatusCode(result.StatusCode, result.Message)
        };
    }
}