import { TestBed } from '@angular/core/testing';
import { TokenService } from './token.service';

describe('TokenService', () => {
    let service: TokenService;

    const mockToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImdyb3VwcyI6WyJBRE1JTiJdLCJleHAiOjE5MDk4ODI4MDB9.SIGNATURE'; // exp: 2030 (approx)
    const expiredToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImdyb3VwcyI6WyJBRE1JTiJdLCJleHAiOjE1MTAwMDAwMDB9.SIGNATURE'; // exp: 2017

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(TokenService);

        // Clear storage before each test
        localStorage.clear();
        sessionStorage.clear();
    });

    afterEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    describe('setTokens', () => {
        it('should store tokens in localStorage when persist is true', () => {
            service.setTokens('acc', 'ref', true);
            expect(localStorage.getItem('access_token')).toBe('acc');
            expect(localStorage.getItem('refresh_token')).toBe('ref');
            expect(sessionStorage.getItem('access_token')).toBeNull();
        });

        it('should store tokens in sessionStorage when persist is false', () => {
            service.setTokens('acc', 'ref', false);
            expect(sessionStorage.getItem('access_token')).toBe('acc');
            expect(sessionStorage.getItem('refresh_token')).toBe('ref');
            expect(localStorage.getItem('access_token')).toBeNull();
        });
    });

    describe('getAccessToken / getRefreshToken', () => {
        it('should retrieve token from localStorage if present', () => {
            localStorage.setItem('access_token', 'local-acc');
            expect(service.getAccessToken()).toBe('local-acc');
        });

        it('should retrieve token from sessionStorage if present', () => {
            sessionStorage.setItem('access_token', 'session-acc');
            expect(service.getAccessToken()).toBe('session-acc');
        });
    });

    describe('isAuthenticated', () => {
        it('should return true if token is valid and not expired', () => {
            // Mock getAccessToken
            spyOn(service, 'getAccessToken').and.returnValue(mockToken);
            // We rely on actual decode logic, so we need a valid structure token (done above)
            expect(service.isAuthenticated()).toBeTrue();
        });

        it('should return false if token is expired', () => {
            spyOn(service, 'getAccessToken').and.returnValue(expiredToken);
            expect(service.isAuthenticated()).toBeFalse();
        });

        it('should return false if no token', () => {
            spyOn(service, 'getAccessToken').and.returnValue(null);
            expect(service.isAuthenticated()).toBeFalse();
        });
    });

    describe('Role checks', () => {
        it('should identify admin role', () => {
            spyOn(service, 'getAccessToken').and.returnValue(mockToken);
            expect(service.isAdmin()).toBeTrue();
        });

        it('should check specific roles', () => {
            spyOn(service, 'getAccessToken').and.returnValue(mockToken);
            expect(service.hasRole('ADMIN')).toBeTrue();
            expect(service.hasRole('USER')).toBeFalse();
        });
    });

    describe('willExpireSoon', () => {
        it('should return true if token expires within threshold', () => {
            // Create a token that expires in 1 minute
            const nowSec = Math.floor(Date.now() / 1000);
            const exp = nowSec + 60;
            // Manually mocking decodeToken to avoid generating a real JWT signature
            spyOn(service, 'getAccessToken').and.returnValue('token');
            spyOn(service, 'decodeToken').and.returnValue({ sub: 'user', groups: [], exp: exp } as any);

            expect(service.willExpireSoon(5)).toBeTrue();
        });

        it('should return false if token expires later', () => {
            const nowSec = Math.floor(Date.now() / 1000);
            const exp = nowSec + 3600; // 1 hour
            spyOn(service, 'getAccessToken').and.returnValue('token');
            spyOn(service, 'decodeToken').and.returnValue({ sub: 'user', groups: [], exp: exp } as any);

            expect(service.willExpireSoon(5)).toBeFalse();
        });
    });
});
