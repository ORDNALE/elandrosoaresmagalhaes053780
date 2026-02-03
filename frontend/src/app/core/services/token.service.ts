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
     * Store tokens in storage
     * @param persist If true, use localStorage (Remember Me). If false, use sessionStorage.
     */
    setTokens(accessToken: string, refreshToken: string, persist: boolean): void {
        const storage = persist ? localStorage : sessionStorage;
        const otherStorage = persist ? sessionStorage : localStorage;

        // Save to target storage
        storage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
        storage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);

        // Clear other storage to avoid conflicts
        otherStorage.removeItem(this.ACCESS_TOKEN_KEY);
        otherStorage.removeItem(this.REFRESH_TOKEN_KEY);
    }

    /**
     * Get access token from either storage
     */
    getAccessToken(): string | null {
        return localStorage.getItem(this.ACCESS_TOKEN_KEY) || sessionStorage.getItem(this.ACCESS_TOKEN_KEY);
    }

    /**
     * Get refresh token from either storage
     */
    getRefreshToken(): string | null {
        return localStorage.getItem(this.REFRESH_TOKEN_KEY) || sessionStorage.getItem(this.REFRESH_TOKEN_KEY);
    }

    /**
     * Clear all tokens (logout)
     */
    clearTokens(): void {
        localStorage.removeItem(this.ACCESS_TOKEN_KEY);
        localStorage.removeItem(this.REFRESH_TOKEN_KEY);
        sessionStorage.removeItem(this.ACCESS_TOKEN_KEY);
        sessionStorage.removeItem(this.REFRESH_TOKEN_KEY);
    }

    /**
     * Save email for Remember Me functionality
     */
    setRememberedEmail(email: string): void {
        localStorage.setItem(this.REMEMBERED_EMAIL_KEY, email);
    }

    /**
     * Get remembered email
     */
    getRememberedEmail(): string | null {
        return localStorage.getItem(this.REMEMBERED_EMAIL_KEY);
    }

    /**
     * Clear remembered email
     */
    clearRememberedEmail(): void {
        localStorage.removeItem(this.REMEMBERED_EMAIL_KEY);
    }

    /**
     * Check if user is authenticated (has valid access token)
     */
    isAuthenticated(): boolean {
        const token = this.getAccessToken();
        if (!token) return false;

        // Check if token is expired
        const decoded = this.decodeToken(token);
        if (!decoded) return false;

        const now = Math.floor(Date.now() / 1000);
        return decoded.exp > now;
    }

    /**
     * Decode JWT token
     */
    decodeToken(token: string): DecodedToken | null {
        try {
            const payload = token.split('.')[1];
            const decoded = JSON.parse(atob(payload));
            return decoded as DecodedToken;
        } catch (error) {
            console.error('Error decoding token:', error);
            return null;
        }
    }

    /**
     * Get user roles from token
     */
    getUserRoles(): string[] {
        const token = this.getAccessToken();
        if (!token) return [];

        const decoded = this.decodeToken(token);
        return decoded?.groups || [];
    }

    /**
     * Check if user has specific role
     */
    hasRole(role: string): boolean {
        return this.getUserRoles().includes(role);
    }

    /**
     * Check if user is admin
     */
    isAdmin(): boolean {
        return this.hasRole('ADMIN');
    }

    /**
     * Get username from token
     */
    getUsername(): string | null {
        const token = this.getAccessToken();
        if (!token) return null;

        const decoded = this.decodeToken(token);
        return decoded?.sub || null;
    }

    /**
     * Check if token will expire soon (within specific minutes)
     */
    willExpireSoon(thresholdMinutes: number = 2): boolean {
        const token = this.getAccessToken();
        if (!token) return false;

        const decoded = this.decodeToken(token);
        if (!decoded) return false;

        const now = Math.floor(Date.now() / 1000);
        const thresholdSeconds = thresholdMinutes * 60;

        // Return true if remaining time is less than threshold
        return (decoded.exp - now) < thresholdSeconds;
    }
}