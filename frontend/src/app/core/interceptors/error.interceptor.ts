import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError, switchMap, EMPTY } from 'rxjs';
import { TokenService } from '../services/token.service';
import { AuthApiService } from '../services/api';
import { NotificationService } from '../services/notification.service';

/**
 * HTTP Interceptor to handle errors globally
 * - 401: Attempt token refresh or redirect to login
 * - 403: Forbidden (insufficient permissions)
 * - 404: Not found
 * - 500: Server error
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);
    const tokenService = inject(TokenService);
    const authApiService = inject(AuthApiService);
    const notificationService = inject(NotificationService);

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            // Handle 429 Too Many Requests
            if (error.status === 429) {
                notificationService.warning('Muitas requisições. Por favor, aguarde alguns instantes.');
                return EMPTY;
            }

            // Handle 401 Unauthorized
            if (error.status === 401) {
                // If it's a refresh token request that failed, logout
                if (req.url.includes('/auth/refresh')) {
                    tokenService.clearTokens();
                    router.navigate(['/login']);
                    return throwError(() => error);
                }

                // Try to refresh token
                const refreshToken = tokenService.getRefreshToken();
                if (refreshToken) {
                    return authApiService.refreshToken({ refreshToken }).pipe(
                        switchMap((response) => {
                            // Update tokens
                            // Preserve persistence choice
                            const isPersistent = !!localStorage.getItem('access_token');
                            tokenService.setTokens(response.accessToken, response.refreshToken, isPersistent);

                            // Retry original request with new token
                            // The AuthInterceptor will pick up the new token automatically
                            return next(req);
                        }),
                        catchError((refreshError) => {
                            // If refresh fails, logout
                            tokenService.clearTokens();
                            router.navigate(['/login']);
                            return throwError(() => refreshError);
                        })
                    );
                } else {
                    tokenService.clearTokens();
                    router.navigate(['/login']);
                    return throwError(() => error);
                }
            }

            // Handle 403 Forbidden
            if (error.status === 403) {
                console.error('Access denied:', error.message);
                // Could show a toast notification here
            }

            // Handle 404 Not Found
            if (error.status === 404) {
                console.error('Resource not found:', error.message);
            }

            // Handle 500 Server Error
            if (error.status === 500) {
                console.error('Server error:', error.message);
            }

            return throwError(() => error);
        })
    );
};
