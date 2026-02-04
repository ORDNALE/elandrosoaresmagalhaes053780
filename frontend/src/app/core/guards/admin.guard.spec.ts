import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { adminGuard } from './admin.guard';
import { TokenService } from '../services/token.service';

describe('AdminGuard', () => {
    let tokenServiceSpy: jasmine.SpyObj<TokenService>;
    let routerSpy: jasmine.SpyObj<Router>;

    beforeEach(() => {
        tokenServiceSpy = jasmine.createSpyObj('TokenService', ['isAuthenticated', 'isAdmin']);
        routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        TestBed.configureTestingModule({
            providers: [
                { provide: TokenService, useValue: tokenServiceSpy },
                { provide: Router, useValue: routerSpy }
            ]
        });
    });

    it('deve permitir acesso se autenticado E admin', () => {
        tokenServiceSpy.isAuthenticated.and.returnValue(true);
        tokenServiceSpy.isAdmin.and.returnValue(true);

        const result = TestBed.runInInjectionContext(() => adminGuard({} as any, {} as any));

        expect(result).toBeTrue();
    });

    it('deve redirecionar para login se NÃO autenticado', () => {
        tokenServiceSpy.isAuthenticated.and.returnValue(false);

        const result = TestBed.runInInjectionContext(() => adminGuard({} as any, {} as any));

        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
        expect(result).toBeFalse();
    });

    it('deve redirecionar para artistas se autenticado mas NÃO admin', () => {
        tokenServiceSpy.isAuthenticated.and.returnValue(true);
        tokenServiceSpy.isAdmin.and.returnValue(false);

        const result = TestBed.runInInjectionContext(() => adminGuard({} as any, {} as any));

        expect(routerSpy.navigate).toHaveBeenCalledWith(['/artists']);
        expect(result).toBeFalse();
    });
});
