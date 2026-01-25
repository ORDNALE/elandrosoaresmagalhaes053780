package com.elandroapi.core.auth;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtService {

    // iss → quem emitiu o token (API)
    // sub → quem é o usuário autenticado
    // exp → quando o token expira
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
