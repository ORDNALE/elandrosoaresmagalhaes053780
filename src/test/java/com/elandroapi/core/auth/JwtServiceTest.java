package com.elandroapi.core.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class JwtServiceTest {

    @Inject
    JwtService jwtService;

    @Inject
    JWTParser jwtParser;

    private static final String USERNAME = "testuser";

    @Test
    void deveGerarTokenDeAcessoComClaimsCorretos() throws ParseException {
        String token = jwtService.gerarToken(USERNAME);
        JsonWebToken jwt = jwtParser.parse(token);

        assertNotNull(token);
        assertEquals("elandro-api", jwt.getIssuer());
        assertEquals(USERNAME, jwt.getSubject());
        assertTrue(jwt.getGroups().contains("USER"));
        assertTrue(jwt.getGroups().contains("ADMIN"));

        // Verifica a expiração (aproximadamente 5 minutos)
        long expirationTime = jwt.getExpirationTime();
        long issuedAtTime = jwt.getIssuedAtTime();
        long duration = expirationTime - issuedAtTime;

        assertTrue(duration >= 299 && duration <= 300, "A duração do token deve ser de 5 minutos");
    }

    @Test
    void deveGerarRefreshTokenComClaimsCorretos() throws ParseException {
        String token = jwtService.gerarRefreshToken(USERNAME);
        JsonWebToken jwt = jwtParser.parse(token);

        assertNotNull(token);
        assertEquals("elandro-api", jwt.getIssuer());
        assertEquals(USERNAME, jwt.getSubject());
        assertEquals("refresh", jwt.getClaim("type"));
        assertTrue(jwt.getGroups() == null || jwt.getGroups().isEmpty(), "Refresh token não deve ter grupos");

        // Verifica a expiração (aproximadamente 30 minutos)
        long expirationTime = jwt.getExpirationTime();
        long issuedAtTime = jwt.getIssuedAtTime();
        long duration = expirationTime - issuedAtTime;

        assertTrue(duration >= 1799 && duration <= 1800, "A duração do token deve ser de 30 minutos");
    }
}
