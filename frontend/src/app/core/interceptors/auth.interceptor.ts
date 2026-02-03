import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';

/**
 * HTTP interceptor that adds JWT token to outgoing requests.
 * Skips adding token for auth endpoints.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    // Skip adding token for auth endpoints
    if (req.url.includes('/auth/')) {
        return next(req);
    }

    const tokenService = inject(TokenService);
    const token = tokenService.getAccessToken();

    if (token) {
        const authReq = req.clone({
            headers: req.headers.set('Authorization', `Bearer ${token}`)
        });
        return next(authReq);
    }

    return next(req);
};