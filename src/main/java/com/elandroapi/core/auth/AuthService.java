package com.elandroapi.core.auth;

@ApplicationScoped
public class AuthService {

    @Inject
    JwtService jwtService;

    @Inject
    JWTParser jwtParser;

    public TokenResponse login(LoginRequest request) {

        // 🔐 Simples de propósito (teste técnico)
        if (!"admin".equals(request.username())) {
            throw new WebApplicationException("Usuário inválido", 401);
        }

        String accessToken = jwtService.gerarToken(request.username());
        String refreshToken = jwtService.gerarRefreshToken(request.username());

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refresh(TokenRefreshRequest request) {

        JsonWebToken jwt = jwtParser.parse(request.refreshToken());

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
