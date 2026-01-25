package com.elandroapi.core.auth;

import com.elandroapi.modules.dto.request.LoginRequest;
import com.elandroapi.modules.dto.request.TokenRefreshRequest;
import com.elandroapi.modules.dto.response.TokenResponse;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/v1/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    @PermitAll
    public TokenResponse login(LoginRequest request) {
        return authService.login(request);
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public TokenResponse refresh(TokenRefreshRequest request) throws ParseException {
        return authService.refresh(request);
    }
}
