import { Injectable } from '@angular/core';
import { DecodedToken } from '@core/models';

@Injectable({
    providedIn: 'root'
})
export class TokenService {
    private readonly ACCESS_TOKEN_KEY = 'access_token';
    private readonly REFRESH_TOKEN_KEY = 'refresh_token';
    private readonly REMEMBERED_EMAIL_KEY = 'remembered_email';

    /**
     * Armazena os tokens no armazenamento (local ou session)
     * @param persist Se verdadeiro, usa localStorage (Lembrar de mim). Se falso, usa sessionStorage.
     */
    setTokens(accessToken: string, refreshToken: string, persist: boolean): void {
        const storage = persist ? localStorage : sessionStorage;
        const otherStorage = persist ? sessionStorage : localStorage;

        storage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
        storage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);

        // Limpa o outro armazenamento para evitar conflitos
        otherStorage.removeItem(this.ACCESS_TOKEN_KEY);
        otherStorage.removeItem(this.REFRESH_TOKEN_KEY);
    }

    /**
     * Obtém o token de acesso de qualquer armazenamento
     */
    getAccessToken(): string | null {
        return localStorage.getItem(this.ACCESS_TOKEN_KEY) || sessionStorage.getItem(this.ACCESS_TOKEN_KEY);
    }

    /**
     * Obtém o token de atualização de qualquer armazenamento
     */
    getRefreshToken(): string | null {
        return localStorage.getItem(this.REFRESH_TOKEN_KEY) || sessionStorage.getItem(this.REFRESH_TOKEN_KEY);
    }

    /**
     * Limpa todos os tokens (logout)
     */
    clearTokens(): void {
        localStorage.removeItem(this.ACCESS_TOKEN_KEY);
        localStorage.removeItem(this.REFRESH_TOKEN_KEY);
        sessionStorage.removeItem(this.ACCESS_TOKEN_KEY);
        sessionStorage.removeItem(this.REFRESH_TOKEN_KEY);
    }

    /**
     * Salva o email para a funcionalidade "Lembrar de mim"
     */
    setRememberedEmail(email: string): void {
        localStorage.setItem(this.REMEMBERED_EMAIL_KEY, email);
    }

    /**
     * Obtém o email lembrado
     */
    getRememberedEmail(): string | null {
        return localStorage.getItem(this.REMEMBERED_EMAIL_KEY);
    }

    /**
     * Limpa o email lembrado
     */
    clearRememberedEmail(): void {
        localStorage.removeItem(this.REMEMBERED_EMAIL_KEY);
    }

    /**
     * Verifica se o usuário está autenticado (possui token válido)
     */
    isAuthenticated(): boolean {
        const token = this.getAccessToken();
        if (!token) return false;

        const decoded = this.decodeToken(token);
        if (!decoded) return false;

        const now = Math.floor(Date.now() / 1000);
        return decoded.exp > now;
    }

    /**
     * Decodifica o token JWT
     */
    decodeToken(token: string): DecodedToken | null {
        try {
            const payload = token.split('.')[1];
            const decoded = JSON.parse(atob(payload));
            return decoded as DecodedToken;
        } catch (error) {
            console.error('Erro ao decodificar token:', error);
            return null;
        }
    }

    /**
     * Obtém as roles do usuário a partir do token
     */
    getUserRoles(): string[] {
        const token = this.getAccessToken();
        if (!token) return [];

        const decoded = this.decodeToken(token);
        return decoded?.groups || [];
    }

    /**
     * Verifica se o usuário possui uma role específica
     */
    hasRole(role: string): boolean {
        return this.getUserRoles().includes(role);
    }

    /**
     * Verifica se o usuário é admin
     */
    isAdmin(): boolean {
        return this.hasRole('ADMIN');
    }

    /**
     * Obtém o nome de usuário (sub) do token
     */
    getUsername(): string | null {
        const token = this.getAccessToken();
        if (!token) return null;

        const decoded = this.decodeToken(token);
        return decoded?.sub || null;
    }

    /**
     * Verifica se o token expirará em breve (dentro dos minutos especificados)
     */
    willExpireSoon(thresholdMinutes: number = 2): boolean {
        const token = this.getAccessToken();
        if (!token) return false;

        const decoded = this.decodeToken(token);
        if (!decoded) return false;

        const now = Math.floor(Date.now() / 1000);
        const thresholdSeconds = thresholdMinutes * 60;

        return (decoded.exp - now) < thresholdSeconds;
    }
}