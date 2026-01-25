package com.elandroapi.core.auth;

@ApplicationScoped
public class JwtService {

    private static final String ISSUER = "elandro-api";

    public String gerarToken(String username) {
        return Jwt.issuer(ISSUER)
                .subject(username)
                .groups(Set.of("USER", "ADMIN"))
                .expiresIn(Duration.ofMinutes(5))
                .sign();
    }

    public String gerarRefreshToken(String username) {
        return Jwt.issuer(ISSUER)
                .subject(username)
                .claim("type", "refresh")
                .expiresIn(Duration.ofMinutes(30))
                .sign();
    }
}
