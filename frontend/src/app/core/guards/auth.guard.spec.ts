import { TestBed } from '@angular/core/testing';
import { Router, UrlTree } from '@angular/router';
import { authGuard } from './auth.guard';
import { TokenService } from '../services/token.service';

describe('AuthGuard', () => {
    let tokenServiceSpy: jasmine.SpyObj<TokenService>;
    let routerSpy: jasmine.SpyObj<Router>;

    beforeEach(() => {
        tokenServiceSpy = jasmine.createSpyObj('TokenService', ['isAuthenticated']);
        routerSpy = jasmine.createSpyObj('Router', ['createUrlTree']);

        TestBed.configureTestingModule({
            providers: [
                { provide: TokenService, useValue: tokenServiceSpy },
                { provide: Router, useValue: routerSpy }
            ]
        });
    });

    it('should allow access if authenticated', () => {
        tokenServiceSpy.isAuthenticated.and.returnValue(true);

        const result = TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));

        expect(result).toBeTrue();
    });

    it('should redirect to login if NOT authenticated', () => {
        tokenServiceSpy.isAuthenticated.and.returnValue(false);
        const mockUrlTree = {} as UrlTree;
        routerSpy.createUrlTree.and.returnValue(mockUrlTree);

        const result = TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));

        expect(routerSpy.createUrlTree).toHaveBeenCalledWith(['/login']);
        expect(result).toBe(mockUrlTree);
    });
});
