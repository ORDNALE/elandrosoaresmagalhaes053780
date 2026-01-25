package com.elandroapi.core.auth;

import com.elandroapi.modules.dto.request.LoginRequest;
import com.elandroapi.modules.dto.request.TokenRefreshRequest;
import com.elandroapi.modules.dto.response.TokenResponse;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/v1/auth")
@Tag(name = "Login", description = "Autenticação do usuário")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Autenticação do usuário")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Autenticação realizada com sucesso"
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Usuário ou senha inválidos"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Requisição inválida"
            )
    })
    public TokenResponse login(LoginRequest request) {
        return authService.login(request);
    }

    @POST
    @Path("/refresh")
    @PermitAll
    @Operation(summary = "Renovação do access token via refresh token")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Token renovado com sucesso"
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Refresh token inválido ou expirado"
            )
    })
    public TokenResponse refresh(TokenRefreshRequest request) throws ParseException {
        return authService.refresh(request);
    }
}

