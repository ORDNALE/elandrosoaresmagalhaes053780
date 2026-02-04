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

    it('should allow access if authenticated AND admin', () => {
        tokenServiceSpy.isAuthenticated.and.returnValue(true);
        tokenServiceSpy.isAdmin.and.returnValue(true);

        const result = TestBed.runInInjectionContext(() => adminGuard({} as any, {} as any));

        expect(result).toBeTrue();
    });

    it('should redirect to login if NOT authenticated', () => {
        tokenServiceSpy.isAuthenticated.and.returnValue(false);

        const result = TestBed.runInInjectionContext(() => adminGuard({} as any, {} as any));

        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
        expect(result).toBeFalse();
    });

    it('should redirect to artists if authenticated but NOT admin', () => {
        tokenServiceSpy.isAuthenticated.and.returnValue(true);
        tokenServiceSpy.isAdmin.and.returnValue(false);

        const result = TestBed.runInInjectionContext(() => adminGuard({} as any, {} as any));

        expect(routerSpy.navigate).toHaveBeenCalledWith(['/artists']);
        expect(result).toBeFalse();
    });
});
