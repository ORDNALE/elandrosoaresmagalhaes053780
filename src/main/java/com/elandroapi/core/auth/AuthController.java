package com.elandroapi.core.auth;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    private final JwtService jwtService = new JwtService();

    @POST
    @Path("/login")
    @PermitAll
    public TokenResponse login(LoginRequest request) {

        // 🔴 validação fake (suficiente para teste técnico)
        if (!"admin".equals(request.username())) {
            throw new WebApplicationException("Usuário inválido", 401);
        }

        String accessToken = jwtService.gerarToken(request.username());
        String refreshToken = jwtService.gerarRefreshToken(request.username());

        return new TokenResponse(accessToken, refreshToken);
    }

    @POST
    @Path("/refresh")
    @PermitAll
    public TokenResponse refresh(TokenRefreshRequest request) {

        // valida refresh token (simplificado)
        String username = jwtService.validarRefreshToken(request.refreshToken());

        String novoAccessToken = jwtService.gerarToken(username);

        return new TokenResponse(novoAccessToken, request.refreshToken());
    }
}
