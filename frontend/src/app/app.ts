import { Component, signal, inject, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthFacade } from '@core/facades';
import { WebSocketService } from '@core/services/websocket/websocket.service';
import { ToastService } from './shared/services/toast.service';
import { Subject } from 'rxjs';
import { takeUntil, map, distinctUntilChanged } from 'rxjs/operators';
import { HeaderComponent } from './shared/components/header/header.component';
import { ToastContainerComponent } from './shared/components/toast-container/toast-container.component';
import { ConfirmationDialogComponent } from './shared/components/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, ToastContainerComponent, ConfirmationDialogComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit, OnDestroy {
  private readonly authFacade = inject(AuthFacade);
  private readonly wsService = inject(WebSocketService);
  private readonly toastService = inject(ToastService);
  private readonly destroy$ = new Subject<void>();

  protected readonly title = signal('elandro-music');

  ngOnInit(): void {
    // Initialize auth state from stored tokens on app startup
    this.authFacade.initializeAuth();

    // Manage WebSocket connection based on authentication state
    this.authFacade.state$
      .pipe(
        map(state => state.isAuthenticated),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(isAuthenticated => {
        if (isAuthenticated) {
          this.wsService.connect();
        } else {
          try {
            // Safe disconnect if not connected or already closed, handled by service
            this.wsService.disconnect();
          } catch (e) {
            console.error('Error disconnecting websocket', e);
          }
        }
      });

    // Global listener for new albums
    this.wsService.onAlbumCreated()
      .pipe(takeUntil(this.destroy$))
      .subscribe(event => {
        // WebSocket is the single source of truth for creation success
        const title = event.message || 'Novo álbum criado';
        const message = event.titulo || 'Álbum adicionado à biblioteca';
        this.toastService.success(title, message);
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
