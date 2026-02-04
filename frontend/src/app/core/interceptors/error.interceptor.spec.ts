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

    it('deve atualizar token em erro 401', () => {
        tokenServiceSpy.getRefreshToken.and.returnValue('refresh-token');
        authApiServiceSpy.refreshToken.and.returnValue(of({ accessToken: 'new-token', refreshToken: 'new-refresh' }));

        httpClient.get('/api/data').subscribe();

        const req = httpMock.expectOne('/api/data');
        req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

        const retryReq = httpMock.expectOne('/api/data');
        expect(retryReq.request.headers.get('Authorization')).toBeNull();

        expect(tokenServiceSpy.setTokens).toHaveBeenCalledWith('new-token', 'new-refresh', jasmine.any(Boolean));
        retryReq.flush({ data: 'success' });
    });

    it('deve fazer logout em 401 se refresh falhar', () => {
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

    it('deve fazer logout em 401 se SEM refresh token', () => {
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

    it('deve forçar logout se 401 vier do endpoint de refresh', () => {
        httpClient.get('/auth/refresh').subscribe({
            error: (error) => expect(error).toBeTruthy()
        });

        const req = httpMock.expectOne('/auth/refresh');
        req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

        expect(tokenServiceSpy.clearTokens).toHaveBeenCalled();
        expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
    });
});
