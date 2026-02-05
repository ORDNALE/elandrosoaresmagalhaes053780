import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { LoginRequest, TokenResponse, TokenRefreshRequest } from '@core/models';

@Injectable({
    providedIn: 'root'
})
export class AuthApiService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/v1/auth`;

    /**
     * POST /v1/auth/login
     * Authenticate user and receive JWT tokens
     */
    login(request: LoginRequest): Observable<TokenResponse> {
        return this.http.post<TokenResponse>(`${this.baseUrl}/login`, request);
    }

    /**
     * POST /v1/auth/refresh
     * Refresh access token using refresh token
     */
    refreshToken(request: TokenRefreshRequest): Observable<TokenResponse> {
        return this.http.post<TokenResponse>(`${this.baseUrl}/refresh`, request);
    }
}
