import { TestBed } from '@angular/core/testing';
import { TokenService } from './token.service';

describe('TokenService', () => {
    let service: TokenService;

    const mockToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImdyb3VwcyI6WyJBRE1JTiJdLCJleHAiOjE5MDk4ODI4MDB9.SIGNATURE'; // exp: 2030 (approx)
    const expiredToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImdyb3VwcyI6WyJBRE1JTiJdLCJleHAiOjE1MTAwMDAwMDB9.SIGNATURE'; // exp: 2017

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(TokenService);

        localStorage.clear();
        sessionStorage.clear();
    });

    afterEach(() => {
        localStorage.clear();
        sessionStorage.clear();
    });

    it('deve ser criado com sucesso', () => {
        expect(service).toBeTruthy();
    });

    describe('setTokens', () => {
        it('deve armazenar no localStorage se persist=true', () => {
            service.setTokens('acc', 'ref', true);
            expect(localStorage.getItem('access_token')).toBe('acc');
            expect(localStorage.getItem('refresh_token')).toBe('ref');
            expect(sessionStorage.getItem('access_token')).toBeNull();
        });

        it('deve armazenar no sessionStorage se persist=false', () => {
            service.setTokens('acc', 'ref', false);
            expect(sessionStorage.getItem('access_token')).toBe('acc');
            expect(sessionStorage.getItem('refresh_token')).toBe('ref');
            expect(localStorage.getItem('access_token')).toBeNull();
        });
    });

    describe('recuperar tokens', () => {
        it('deve pegar do localStorage se existir', () => {
            localStorage.setItem('access_token', 'local-acc');
            expect(service.getAccessToken()).toBe('local-acc');
        });

        it('deve pegar do sessionStorage se existir', () => {
            sessionStorage.setItem('access_token', 'session-acc');
            expect(service.getAccessToken()).toBe('session-acc');
        });
    });

    describe('verificarAutenticacao', () => {
        it('deve retornar true se token válido', () => {
            spyOn(service, 'getAccessToken').and.returnValue(mockToken);
            expect(service.isAuthenticated()).toBeTrue();
        });

        it('deve retornar false se token expirado', () => {
            spyOn(service, 'getAccessToken').and.returnValue(expiredToken);
            expect(service.isAuthenticated()).toBeFalse();
        });

        it('deve retornar false se não houver token', () => {
            spyOn(service, 'getAccessToken').and.returnValue(null);
            expect(service.isAuthenticated()).toBeFalse();
        });
    });

    describe('Verificação de Roles', () => {
        it('deve identificar role de admin', () => {
            spyOn(service, 'getAccessToken').and.returnValue(mockToken);
            expect(service.isAdmin()).toBeTrue();
        });

        it('deve verificar roles específicas', () => {
            spyOn(service, 'getAccessToken').and.returnValue(mockToken);
            expect(service.hasRole('ADMIN')).toBeTrue();
            expect(service.hasRole('USER')).toBeFalse();
        });
    });

    describe('vaiExpirarEmBreve', () => {
        it('deve retornar true se expirar dentro do limite', () => {
            const nowSec = Math.floor(Date.now() / 1000);
            const exp = nowSec + 60;
            spyOn(service, 'getAccessToken').and.returnValue('token');
            spyOn(service, 'decodeToken').and.returnValue({ sub: 'user', groups: [], exp: exp } as any);

            expect(service.willExpireSoon(5)).toBeTrue();
        });

        it('deve retornar false se expirar depois', () => {
            const nowSec = Math.floor(Date.now() / 1000);
            const exp = nowSec + 3600;
            spyOn(service, 'getAccessToken').and.returnValue('token');
            spyOn(service, 'decodeToken').and.returnValue({ sub: 'user', groups: [], exp: exp } as any);

            expect(service.willExpireSoon(5)).toBeFalse();
        });
    });
});
