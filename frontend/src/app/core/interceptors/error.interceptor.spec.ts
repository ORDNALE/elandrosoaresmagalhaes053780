import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { errorInterceptor } from './error.interceptor';
import { TokenService } from '../services/token.service';
import { AuthApiService } from '../services/api';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('ErrorInterceptor', () => {
    let httpMock: HttpTestingController;
    let httpClient: HttpClient;
    let tokenServiceSpy: jasmine.SpyObj<TokenService>;
    let authApiServiceSpy: jasmine.SpyObj<AuthApiService>;
    let routerSpy: jasmine.SpyObj<Router>;

    beforeEach(() => {
        tokenServiceSpy = jasmine.createSpyObj('TokenService', ['clearTokens', 'getRefreshToken', 'setTokens']);
        authApiServiceSpy = jasmine.createSpyObj('AuthApiService', ['refreshToken']);
        routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        TestBed.configureTestingModule({
            providers: [
                provideHttpClient(withInterceptors([errorInterceptor])),
                provideHttpClientTesting(),
                { provide: TokenService, useValue: tokenServiceSpy },
                { provide: AuthApiService, useValue: authApiServiceSpy },
                { provide: Router, useValue: routerSpy }
            ]
        });

        httpMock = TestBed.inject(HttpTestingController);
        httpClient = TestBed.inject(HttpClient);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should refresh token on 401 error', () => {
        tokenServiceSpy.getRefreshToken.and.returnValue('refresh-token');
        authApiServiceSpy.refreshToken.and.returnValue(of({ accessToken: 'new-token', refreshToken: 'new-refresh' }));

        httpClient.get('/api/data').subscribe();

        const req = httpMock.expectOne('/api/data');
        req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

        // Expect refresh token call inside interceptor logic?
        // Wait, since we mock the service, the interceptor calls it directly.
        // However, the interceptor returns an Observable that retries the request.
        // The retry logic is: next(req) again.

        // Since we returned `of(...)` from refreshToken spy, the switchMap proceeds to `next(req)`.
        // This creates a NEW HTTP request to '/api/data'.
        const retryReq = httpMock.expectOne('/api/data');
        expect(retryReq.request.headers.get('Authorization')).toBeNull(); // It won't have it unless AuthInterceptor is also there or added manually, 
        // BUT the ErrorInterceptor logic calls `next(req)` again. 
        // If AuthInterceptor was in the chain, it would add the token. Here we test ErrorInterceptor in isolation.

        expect(tokenServiceSpy.setTokens).toHaveBeenCalledWith('new-token', 'new-refresh', jasmine.any(Boolean));
        retryReq.flush({ data: 'success' });
    });

    it('should logout on 401 if refresh fails', () => {
        tokenServiceSpy.getRefreshToken.and.returnValue('refresh-token');
        authApiServiceSpy.refreshToken.and.returnValue(throwError(() => new Error('Refresh failed')));

        httpClient.get('/api/data').subscribe({
            error: (error) => {
                expect(error).toBeTruthy();
            }
        });

        const req = httpMock.expectOne('/api/data');
        req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

        expect(tokenServiceSpy.clearTokens).toHaveBeenCalled();
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should logout on 401 if NO refresh token available', () => {
        tokenServiceSpy.getRefreshToken.and.returnValue(null);

        httpClient.get('/api/data').subscribe({
            error: (error) => {
                expect(error).toBeTruthy();
            }
        });

        const req = httpMock.expectOne('/api/data');
        req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

        expect(tokenServiceSpy.clearTokens).toHaveBeenCalled();
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should force logout if 401 comes from refresh endpoint', () => {
        // In this case, the URL is /auth/refresh
        // We need to simulate a request to that URL failing
        httpClient.get('/auth/refresh').subscribe({
            error: (error) => expect(error).toBeTruthy()
        });

        const req = httpMock.expectOne('/auth/refresh');
        req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

        expect(tokenServiceSpy.clearTokens).toHaveBeenCalled();
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
    });
});
