import { Injectable, inject, OnDestroy, signal } from '@angular/core';
import { Router } from '@angular/router';
import { interval, Subscription, fromEvent, merge } from 'rxjs';
import { throttleTime, finalize } from 'rxjs/operators';
import { AuthApiService } from '../services/api';
import { TokenService } from '../services/token.service';
import { AuthStateService } from '../state';
import { NotificationService } from '../services/notification.service';
import { DialogService } from '../services/dialog.service';
import { LoginRequest, ApiError } from '@core/models';
import { HttpErrorResponse } from '@angular/common/http';

/**
 * Auth Facade - Orchestrates authentication operations
 * Manages interaction between AuthApiService, TokenService, and AuthStateService
 */
@Injectable({
  providedIn: 'root'
})
export class AuthFacade implements OnDestroy {
  private readonly authApi = inject(AuthApiService);
  private readonly tokenService = inject(TokenService);
  private readonly authState = inject(AuthStateService);
  private readonly router = inject(Router);
  private readonly notification = inject(NotificationService);
  private readonly dialogService = inject(DialogService);

  // Expose state observables
  readonly state$ = this.authState.state$;
  readonly isLoading$ = this.authState.isLoading$;
  readonly error$ = this.authState.error$;

  // Session state
  readonly isSessionValid = signal(true);
  private sessionMonitorSub?: Subscription;
  private userActivitySub?: Subscription;
  private lastActivity = Date.now();

  constructor() {
    this.initializeAuth();
  }

  ngOnDestroy(): void {
    this.stopSessionMonitoring();
  }

  /**
   * Start monitoring session and user activity
   */
  private startSessionMonitoring(): void {
    this.stopSessionMonitoring();

    // Monitor user activity (mouse moves, key presses)
    const activityEvents$ = merge(
      fromEvent(document, 'mousemove'),
      fromEvent(document, 'keydown'),
      fromEvent(document, 'click')
    );

    this.userActivitySub = activityEvents$
      .pipe(throttleTime(1000)) // Limit updates to once per second
      .subscribe(() => {
        this.lastActivity = Date.now();
      });

    // Check token validity periodically (every 30 seconds)
    this.sessionMonitorSub = interval(30000).subscribe(() => {
      if (this.tokenService.willExpireSoon(2)) { // If less than 2 mins remaining
        this.attemptSilentRefresh();
      }
    });
  }

  private stopSessionMonitoring(): void {
    this.sessionMonitorSub?.unsubscribe();
    this.userActivitySub?.unsubscribe();
  }

  /**
   * Attempt to refresh token silently if user is active
   */
  private attemptSilentRefresh(): void {
    const fiveMinutes = 5 * 60 * 1000;
    const now = Date.now();
    const isUserActive = (now - this.lastActivity) < fiveMinutes;

    if (isUserActive) {
      const refreshToken = this.tokenService.getRefreshToken();
      if (refreshToken) {
        this.authApi.refreshToken({ refreshToken }).subscribe({
          next: (response) => {
            console.log('Silent refresh successful');
            // Update tokens, preserving the persistence choice
            // Check if we are using localStorage currently
            const isPersistent = !!localStorage.getItem('access_token');
            this.tokenService.setTokens(response.accessToken, response.refreshToken, isPersistent);
          },
          error: (err) => {
            console.error('Silent refresh failed', err);
            this.handleSessionExpiry();
          }
        });
      }
    } else {
      console.log('User inactive, skipping silent refresh');
    }
  }

  private async handleSessionExpiry(): Promise<void> {
    this.isSessionValid.set(false);
    this.stopSessionMonitoring();

    await this.dialogService.confirm({
      title: 'Sessão Expirada',
      message: 'Sua conexão expirou. Clique em OK para entrar novamente.',
      confirmText: 'OK',
      cancelText: 'Cancelar',
      type: 'warning'
    });

    this.logout();
  }

  /**
   * Login user
   */
  login(request: LoginRequest, rememberMe: boolean = false): void {
    this.authState.setLoading(true);
    this.authState.clearError();

    this.authApi.login(request)
      .pipe(finalize(() => this.authState.setLoading(false)))
      .subscribe({
        next: (response) => {
          // Store tokens
          this.tokenService.setTokens(response.accessToken, response.refreshToken, rememberMe);

          if (rememberMe) {
            this.tokenService.setRememberedEmail(request.username);
          } else {
            this.tokenService.clearRememberedEmail();
          }

          // Decode token and update state
          const decoded = this.tokenService.decodeToken(response.accessToken);
          this.authState.setState({
            isAuthenticated: true,
            accessToken: response.accessToken,
            refreshToken: response.refreshToken,
            username: decoded?.sub || null,
            roles: decoded?.groups || []
          });

          this.notification.success('Login realizado com sucesso!');
          this.startSessionMonitoring();
          this.router.navigate(['/dashboard']);
        },
        error: (error: HttpErrorResponse) => {
          const apiError = error.error as ApiError;
          let message: string;

          if (error.status === 0) {
            message = 'Não foi possível conectar ao servidor. Verifique se o backend está rodando.';
          } else {
            message = apiError?.message || error.message || 'Falha no login. Verifique suas credenciais.';
          }

          this.authState.setError(message);
          this.notification.error(message);
        }
      });
  }

  /**
   * Logout user
   */
  logout(): void {
    this.stopSessionMonitoring();
    this.tokenService.clearTokens();
    this.authState.reset();
    this.router.navigate(['/login']);
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return this.tokenService.isAuthenticated();
  }

  /**
   * Check if user has specific role
   */
  hasRole(role: string): boolean {
    return this.tokenService.hasRole(role);
  }

  /**
   * Check if user is admin
   */
  isAdmin(): boolean {
    return this.tokenService.isAdmin();
  }

  /**
   * Get current username
   */
  getUsername(): string | null {
    return this.tokenService.getUsername();
  }

  /**
   * Get remembered email
   */
  getRememberedEmail(): string | null {
    return this.tokenService.getRememberedEmail();
  }

  /**
   * Initialize auth state from stored tokens
   */
  initializeAuth(): void {
    if (this.tokenService.isAuthenticated()) {
      const token = this.tokenService.getAccessToken();
      const refreshToken = this.tokenService.getRefreshToken();
      const decoded = token ? this.tokenService.decodeToken(token) : null;

      this.authState.setState({
        isAuthenticated: true,
        accessToken: token,
        refreshToken: refreshToken,
        username: decoded?.sub || null,
        roles: decoded?.groups || []
      });

      this.startSessionMonitoring();
    }
  }
}
