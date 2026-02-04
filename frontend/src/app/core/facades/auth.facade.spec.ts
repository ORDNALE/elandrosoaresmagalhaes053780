import { TestBed, fakeAsync, tick, discardPeriodicTasks } from '@angular/core/testing';
import { AuthFacade } from './auth.facade';
import { AuthApiService } from '../services/api';
import { TokenService } from '../services/token.service';
import { AuthStateService } from '../state';
import { NotificationService } from '../services/notification.service';
import { DialogService } from '../services/dialog.service';
import { Router } from '@angular/router';
import { of, throwError, BehaviorSubject } from 'rxjs';
import { LoginRequest, TokenResponse } from '@core/models';
import { HttpErrorResponse } from '@angular/common/http';

describe('AuthFacade', () => {
    let facade: AuthFacade;
    let authApiSpy: jasmine.SpyObj<AuthApiService>;
    let tokenServiceSpy: jasmine.SpyObj<TokenService>;
    let authStateSpy: jasmine.SpyObj<AuthStateService>;
    let routerSpy: jasmine.SpyObj<Router>;
    let notificationSpy: jasmine.SpyObj<NotificationService>;
    let dialogServiceSpy: jasmine.SpyObj<DialogService>;

    const mockTokenResponse: TokenResponse = {
        accessToken: 'mock-access-token',
        refreshToken: 'mock-refresh-token'
    };

    const mockDecodedToken = {
        sub: 'testuser',
        groups: ['ROLE_USER'],
        exp: 1234567890,
        iat: 1234567000
    };

    beforeEach(() => {
        authApiSpy = jasmine.createSpyObj('AuthApiService', ['login', 'refreshToken']);
        tokenServiceSpy = jasmine.createSpyObj('TokenService', [
            'setTokens', 'getAccessToken', 'getRefreshToken', 'clearTokens',
            'decodeToken', 'isAuthenticated', 'hasRole', 'isAdmin', 'getUsername',
            'setRememberedEmail', 'clearRememberedEmail', 'willExpireSoon'
        ]);
        authStateSpy = jasmine.createSpyObj('AuthStateService', [
            'setLoading', 'clearError', 'setState', 'setError', 'reset'
        ]);
        routerSpy = jasmine.createSpyObj('Router', ['navigate']);
        notificationSpy = jasmine.createSpyObj('NotificationService', ['success', 'error']);
        dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm']);

        (authStateSpy as any).state$ = new BehaviorSubject({
            isAuthenticated: false,
            accessToken: null,
            refreshToken: null,
            username: null,
            roles: []
        });
        (authStateSpy as any).isLoading$ = new BehaviorSubject(false);
        (authStateSpy as any).error$ = new BehaviorSubject(null);

        tokenServiceSpy.isAuthenticated.and.returnValue(false);

        TestBed.configureTestingModule({
            providers: [
                AuthFacade,
                { provide: AuthApiService, useValue: authApiSpy },
                { provide: TokenService, useValue: tokenServiceSpy },
                { provide: AuthStateService, useValue: authStateSpy },
                { provide: Router, useValue: routerSpy },
                { provide: NotificationService, useValue: notificationSpy },
                { provide: DialogService, useValue: dialogServiceSpy }
            ]
        });

        facade = TestBed.inject(AuthFacade);
    });

    afterEach(() => {
        facade.ngOnDestroy();
    });

    it('should be created', () => {
        expect(facade).toBeTruthy();
    });

    describe('login', () => {
        const loginRequest: LoginRequest = { username: 'test', password: 'password' };

        it('should handle successful login with rememberMe=false', () => {
            authApiSpy.login.and.returnValue(of(mockTokenResponse));
            tokenServiceSpy.decodeToken.and.returnValue(mockDecodedToken);

            facade.login(loginRequest, false);

            expect(authStateSpy.setLoading).toHaveBeenCalledWith(true);
            expect(authStateSpy.clearError).toHaveBeenCalled();
            expect(authApiSpy.login).toHaveBeenCalledWith(loginRequest);
            expect(tokenServiceSpy.setTokens).toHaveBeenCalledWith(
                mockTokenResponse.accessToken,
                mockTokenResponse.refreshToken,
                false
            );
            expect(tokenServiceSpy.clearRememberedEmail).toHaveBeenCalled();
            expect(authStateSpy.setState).toHaveBeenCalledWith(jasmine.objectContaining({
                isAuthenticated: true,
                username: 'testuser'
            }));
            expect(authStateSpy.setLoading).toHaveBeenCalledWith(false);
            expect(notificationSpy.success).toHaveBeenCalled();
            expect(routerSpy.navigate).toHaveBeenCalledWith(['/artists']);
        });

        it('should handle successful login with rememberMe=true', () => {
            authApiSpy.login.and.returnValue(of(mockTokenResponse));
            tokenServiceSpy.decodeToken.and.returnValue(mockDecodedToken);

            facade.login(loginRequest, true);

            expect(tokenServiceSpy.setTokens).toHaveBeenCalledWith(
                mockTokenResponse.accessToken,
                mockTokenResponse.refreshToken,
                true
            );
            expect(tokenServiceSpy.setRememberedEmail).toHaveBeenCalledWith(loginRequest.username);
        });

        it('should handle login error (connection failure)', () => {
            const error = new HttpErrorResponse({ status: 0 });
            authApiSpy.login.and.returnValue(throwError(() => error));

            facade.login(loginRequest);

            expect(authStateSpy.setError).toHaveBeenCalledWith(jasmine.stringMatching(/não foi possível conectar/i));
            expect(authStateSpy.setLoading).toHaveBeenCalledWith(false);
            expect(notificationSpy.error).toHaveBeenCalled();
        });

        it('should handle login error (api error)', () => {
            const error = new HttpErrorResponse({
                status: 401,
                error: { message: 'Invalid credentials' }
            });
            authApiSpy.login.and.returnValue(throwError(() => error));

            facade.login(loginRequest);

            expect(authStateSpy.setError).toHaveBeenCalledWith('Invalid credentials');
        });
    });

    describe('logout', () => {
        it('should clear tokens, reset state and navigate', () => {
            facade.logout();

            expect(tokenServiceSpy.clearTokens).toHaveBeenCalled();
            expect(authStateSpy.reset).toHaveBeenCalled();
            expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
        });
    });

    describe('initializeAuth', () => {
        it('should not initialize if not authenticated', () => {
            facade.initializeAuth();

            expect(authStateSpy.setState).not.toHaveBeenCalled();
        });

        it('should restore state if authenticated', () => {
            tokenServiceSpy.isAuthenticated.and.returnValue(true);
            tokenServiceSpy.getAccessToken.and.returnValue('stored-token');
            tokenServiceSpy.getRefreshToken.and.returnValue('stored-refresh');
            tokenServiceSpy.decodeToken.and.returnValue(mockDecodedToken);

            facade.initializeAuth();

            expect(authStateSpy.setState).toHaveBeenCalledWith({
                isAuthenticated: true,
                accessToken: 'stored-token',
                refreshToken: 'stored-refresh',
                username: mockDecodedToken.sub,
                roles: mockDecodedToken.groups
            });
        });
    });

    describe('Session Monitoring (Silent Refresh)', () => {
        it('should attempt silent refresh if token expiring and user active', fakeAsync(() => {
            tokenServiceSpy.isAuthenticated.and.returnValue(true);
            tokenServiceSpy.getAccessToken.and.returnValue('token');
            tokenServiceSpy.getRefreshToken.and.returnValue('refresh');
            tokenServiceSpy.decodeToken.and.returnValue(mockDecodedToken);
            tokenServiceSpy.willExpireSoon.and.returnValue(true);

            facade.initializeAuth();

            authApiSpy.refreshToken.and.returnValue(of({
                accessToken: 'new-token',
                refreshToken: 'new-refresh'
            }));

            tick(30000);

            expect(tokenServiceSpy.willExpireSoon).toHaveBeenCalled();
            expect(authApiSpy.refreshToken).toHaveBeenCalled();
            expect(tokenServiceSpy.setTokens).toHaveBeenCalledWith('new-token', 'new-refresh', jasmine.any(Boolean));

            discardPeriodicTasks();
        }));

        it('should NOT refresh if user is inactive', fakeAsync(() => {
            tokenServiceSpy.isAuthenticated.and.returnValue(true);
            tokenServiceSpy.willExpireSoon.and.returnValue(true);
            facade.initializeAuth();

            tick(5 * 60 * 1000 + 1000);

            tick(30000);

            expect(tokenServiceSpy.willExpireSoon).toHaveBeenCalled();
            expect(authApiSpy.refreshToken).not.toHaveBeenCalled();

            discardPeriodicTasks();
        }));
    });
});
