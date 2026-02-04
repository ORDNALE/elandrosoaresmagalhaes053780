import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './auth.interceptor';
import { TokenService } from '../services/token.service';

describe('AuthInterceptor', () => {
    let httpMock: HttpTestingController;
    let httpClient: HttpClient;
    let tokenServiceSpy: jasmine.SpyObj<TokenService>;

    beforeEach(() => {
        tokenServiceSpy = jasmine.createSpyObj('TokenService', ['getAccessToken']);

        TestBed.configureTestingModule({
            providers: [
                provideHttpClient(withInterceptors([authInterceptor])),
                provideHttpClientTesting(),
                { provide: TokenService, useValue: tokenServiceSpy }
            ]
        });

        httpMock = TestBed.inject(HttpTestingController);
        httpClient = TestBed.inject(HttpClient);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should add Authorization header if token exists', () => {
        tokenServiceSpy.getAccessToken.and.returnValue('mock-token');

        httpClient.get('/api/data').subscribe();

        const req = httpMock.expectOne('/api/data');
        expect(req.request.headers.has('Authorization')).toBeTrue();
        expect(req.request.headers.get('Authorization')).toBe('Bearer mock-token');
    });

    it('should NOT add Authorization header if token is missing', () => {
        tokenServiceSpy.getAccessToken.and.returnValue(null);

        httpClient.get('/api/data').subscribe();

        const req = httpMock.expectOne('/api/data');
        expect(req.request.headers.has('Authorization')).toBeFalse();
    });

    it('should skip auth endpoints', () => {
        tokenServiceSpy.getAccessToken.and.returnValue('mock-token');

        httpClient.get('/api/auth/login').subscribe();

        const req = httpMock.expectOne('/api/auth/login');
        expect(req.request.headers.has('Authorization')).toBeFalse();
    });
});
