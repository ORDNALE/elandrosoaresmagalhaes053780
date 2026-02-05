import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { TokenService } from '../services/token.service';

/**
 * Interceptor HTTP que adiciona o token JWT às requisições de saída.
 * Pula a adição do token para endpoints de autenticação.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    // Pula a adição do token para endpoints de autenticação
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
