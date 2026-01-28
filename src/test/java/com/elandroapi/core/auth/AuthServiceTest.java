package com.elandroapi.core.auth;

import com.elandroapi.modules.dto.request.LoginRequest;
import com.elandroapi.modules.dto.request.TokenRefreshRequest;
import com.elandroapi.modules.dto.response.TokenResponse;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    JwtService jwtService;

    @Mock
    JWTParser jwtParser;

    @BeforeEach
    void setUp() throws Exception {
        // Configura as propriedades via reflection (pois são @ConfigProperty)
        setField(authService, "authUsername", "testuser");
        setField(authService, "authPassword", "testpass");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void deveRealizarLoginComSucesso() {
        LoginRequest request = new LoginRequest("testuser", "testpass");
        when(jwtService.gerarToken("testuser")).thenReturn("fake-access-token");
        when(jwtService.gerarRefreshToken("testuser")).thenReturn("fake-refresh-token");

        TokenResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-access-token", response.accessToken());
        assertEquals("fake-refresh-token", response.refreshToken());
    }

    @Test
    void deveLancarExcecaoAoRealizarLoginComCredenciaisInvalidas() {
        LoginRequest request = new LoginRequest("wronguser", "wrongpass");

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            authService.login(request);
        });
        assertEquals(401, exception.getResponse().getStatus());
        assertEquals("Credenciais inválidas", exception.getMessage());
    }

    @Test
    void deveRenovarTokenComSucesso() throws ParseException {
        String validRefreshToken = "valid-refresh-token";
        TokenRefreshRequest request = new TokenRefreshRequest(validRefreshToken);

        JsonWebToken mockJwt = org.mockito.Mockito.mock(JsonWebToken.class);
        when(mockJwt.getIssuer()).thenReturn("elandro-api");
        when(mockJwt.getClaim("type")).thenReturn("refresh");
        when(mockJwt.getSubject()).thenReturn("testuser");

        when(jwtParser.parse(validRefreshToken)).thenReturn(mockJwt);
        when(jwtService.gerarToken("testuser")).thenReturn("new-access-token");

        TokenResponse response = authService.refresh(request);

        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
        assertEquals(validRefreshToken, response.refreshToken());
    }

    @Test
    void deveLancarExcecaoAoRenovarComIssuerInvalido() throws ParseException {
        String tokenComIssuerInvalido = "invalid-issuer-token";
        TokenRefreshRequest request = new TokenRefreshRequest(tokenComIssuerInvalido);

        JsonWebToken mockJwt = org.mockito.Mockito.mock(JsonWebToken.class);
        when(mockJwt.getIssuer()).thenReturn("invalid-issuer");

        when(jwtParser.parse(tokenComIssuerInvalido)).thenReturn(mockJwt);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            authService.refresh(request);
        });
        assertEquals(401, exception.getResponse().getStatus());
        assertEquals("Issuer inválido", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoAoRenovarComClaimTypeInvalido() throws ParseException {
        String tokenComClaimInvalido = "invalid-claim-token";
        TokenRefreshRequest request = new TokenRefreshRequest(tokenComClaimInvalido);

        JsonWebToken mockJwt = org.mockito.Mockito.mock(JsonWebToken.class);
        when(mockJwt.getIssuer()).thenReturn("elandro-api");
        when(mockJwt.getClaim("type")).thenReturn("access");

        when(jwtParser.parse(tokenComClaimInvalido)).thenReturn(mockJwt);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            authService.refresh(request);
        });
        assertEquals(401, exception.getResponse().getStatus());
        assertEquals("Refresh token inválido", exception.getMessage());
    }
}
