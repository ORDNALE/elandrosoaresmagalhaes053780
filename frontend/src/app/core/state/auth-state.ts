import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthState } from '@core/models';

@Injectable({
  providedIn: 'root'
})
export class AuthStateService {
  private readonly initialState: AuthState = {
    isAuthenticated: false,
    accessToken: null,
    refreshToken: null,
    username: null,
    roles: []
  };

  private readonly authState$ = new BehaviorSubject<AuthState>(this.initialState);
  private readonly loading$ = new BehaviorSubject<boolean>(false);
  private readonly errorSubject$ = new BehaviorSubject<string | null>(null);

  // Public observables
  readonly state$: Observable<AuthState> = this.authState$.asObservable();
  readonly isLoading$: Observable<boolean> = this.loading$.asObservable();
  readonly error$: Observable<string | null> = this.errorSubject$.asObservable();

  /**
   * Set authentication state
   */
  setState(state: Partial<AuthState>): void {
    this.authState$.next({
      ...this.authState$.value,
      ...state
    });
  }

  /**
   * Set loading state
   */
  setLoading(loading: boolean): void {
    this.loading$.next(loading);
  }

  /**
   * Set error message
   */
  setError(error: string | null): void {
    this.errorSubject$.next(error);
  }

  /**
   * Clear error
   */
  clearError(): void {
    this.errorSubject$.next(null);
  }

  /**
   * Reset to initial state (logout)
   */
  reset(): void {
    this.authState$.next(this.initialState);
    this.loading$.next(false);
    this.errorSubject$.next(null);
  }

  /**
   * Get current state value
   */
  getCurrentState(): AuthState {
    return this.authState$.value;
  }
}
