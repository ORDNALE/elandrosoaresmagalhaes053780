package com.elandroapi.core.auth;

import com.elandroapi.modules.dto.request.LoginRequest;
import com.elandroapi.modules.dto.request.TokenRefreshRequest;
import com.elandroapi.modules.dto.response.TokenResponse;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class AuthService {

    @Inject
    JwtService jwtService;

    @Inject
    JWTParser jwtParser;

    public TokenResponse login(LoginRequest request) {

        if (!"admin".equals(request.username())
                || !"admin".equals(request.password())) {
            throw new WebApplicationException("Credenciais inválidas", 401);
        }

        String accessToken = jwtService.gerarToken(request.username());
        String refreshToken = jwtService.gerarRefreshToken(request.username());

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refresh(TokenRefreshRequest request) throws ParseException {

        JsonWebToken jwt = jwtParser.parse(request.refreshToken());

        if (!"elandro-api".equals(jwt.getIssuer())) {
            throw new WebApplicationException("Issuer inválido", 401);
        }

        if (!"refresh".equals(jwt.getClaim("type"))) {
            throw new WebApplicationException("Refresh token inválido", 401);
        }

        String username = jwt.getSubject();

        return new TokenResponse(
                jwtService.gerarToken(username),
                request.refreshToken()
        );
    }

}
