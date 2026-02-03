import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { TokenService } from '../services/token.service';

/**
 * Guard Administrativo - Protege rotas que requerem função de ADMIN
 * Verifica se o usuário está autenticado E possui a role ADMIN
 */
export const adminGuard: CanActivateFn = () => {
    const tokenService = inject(TokenService);
    const router = inject(Router);

    if (!tokenService.isAuthenticated()) {
        router.navigate(['/login']);
        return false;
    }

    if (!tokenService.isAdmin()) {
        // User is authenticated but not admin
        console.warn('Access denied: ADMIN role required');
        router.navigate(['/artists']); // Redirect to home
        return false;
    }

    return true;
};