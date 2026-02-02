import { Injectable, inject } from '@angular/core';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { AlbumApiService } from '../services/api';
import { AlbumStateService } from '../state';
import {
  AlbumRequest,
  AlbumResponse,
  AlbumFilterRequest,
  PageRequest,
  Paged,
  ApiError
} from '@core/models';
import { NotificationService } from '../services/notification.service';
import { Router } from '@angular/router';
import { CoverApiService } from '../services/api';
import { HttpErrorResponse } from '@angular/common/http';
import { switchMap, map } from 'rxjs/operators';
import { forkJoin, of } from 'rxjs';
import { WebSocketService } from '../services/websocket/websocket.service';

/**
 * Album Facade - Orchestrates album operations
 * Manages interaction between AlbumApiService and AlbumStateService
 */
@Injectable({
  providedIn: 'root'
})
export class AlbumFacade {
  private readonly albumApi = inject(AlbumApiService);
  private readonly albumState = inject(AlbumStateService);
  private readonly notification = inject(NotificationService);
  private readonly router = inject(Router);
  private readonly coverApi = inject(CoverApiService);
  private readonly wsService = inject(WebSocketService); // Inject WS Service

  // Expose state observables
  readonly albums$ = this.albumState.albumList$;
  readonly selectedAlbum$ = this.albumState.selectedAlbum;
  readonly loading$ = this.albumState.isLoading$;
  readonly error$ = this.albumState.error$;
  readonly page$ = this.albumState.page$;
  readonly pageCount$ = this.albumState.pageCount$;
  readonly total$ = this.albumState.total$;

  /**
   * Load albums list with pagination and filters
   */
  loadAlbums(pageRequest: PageRequest, filter?: AlbumFilterRequest): void {
    this.albumState.setLoading(true);
    this.albumState.clearError();

    this.albumApi.list(pageRequest, filter).subscribe({
      next: (response) => {
        this.albumState.setAlbums(response.content);
        this.albumState.setPagination(
          response.page,
          response.pageCount,
          response.total
        );
        this.albumState.setLoading(false);
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError;
        const message = apiError?.message || error.message || 'Falha ao carregar álbuns';

        this.albumState.setError(message);
        this.albumState.setLoading(false);
        this.notification.error(message);
      }
    });
  }

  /**
   * Load album by ID
   */
  loadAlbumById(id: number): void {
    this.albumState.setLoading(true);
    this.albumState.clearError();

    this.albumApi.getById(id).subscribe({
      next: (album) => {
        this.albumState.setSelectedAlbum(album);
        this.albumState.setLoading(false);
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError;
        const message = apiError?.message || error.message || 'Falha ao carregar álbum';

        this.albumState.setError(message);
        this.albumState.setLoading(false);
        this.notification.error(message);
        this.router.navigate(['/albums']);
      }
    });
  }

  /**
   * Create new album
   */
  createAlbum(request: AlbumRequest, files?: File[]): void {
    this.albumState.setLoading(true);
    this.albumState.clearError();

    this.albumApi.create(request).pipe(
      switchMap(album => {
        if (files && files.length > 0 && album.id) {
          return this.coverApi.upload(album.id, files).pipe(
            map(() => album) // Return original album after upload
          );
        }
        return of(album);
      })
    ).subscribe({
      next: (album) => {
        this.albumState.addAlbum(album);
        this.albumState.setLoading(false);

        // Smart Fallback: Only rely on WS if connected
        if (!this.wsService.isConnected()) {
          console.warn('WS not connected, using fallback notification');
          this.notification.success('Álbum criado com sucesso! (Offline Mode)');
        } else {
          console.log('WS connected, waiting for server event...');
        }

        this.router.navigate(['/albums']);
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError;
        const message = apiError?.message || error.message || 'Falha ao criar álbum';

        this.albumState.setError(message);
        this.albumState.setLoading(false);
        this.notification.error(message);
      }
    });
  }

  /**
   * Update album
   */
  updateAlbum(id: number, request: AlbumRequest, files?: File[]): void {
    this.albumState.setLoading(true);
    this.albumState.clearError();

    this.albumApi.update(id, request).pipe(
      switchMap(() => {
        if (files && files.length > 0) {
          return this.coverApi.upload(id, files);
        }
        return of(null);
      }),
      switchMap(() => this.albumApi.getById(id))
    ).subscribe({
      next: (album) => {
        this.albumState.updateAlbum(id, album);
        this.albumState.setSelectedAlbum(album);
        this.albumState.setLoading(false);
        this.notification.success('Álbum atualizado com sucesso!');
        this.router.navigate(['/albums']);
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError;
        const message = apiError?.message || error.message || 'Falha ao atualizar álbum';

        this.albumState.setError(message);
        this.albumState.setLoading(false);
        this.notification.error(message);
      }
    });
  }

  /**
   * Delete album
   */
  deleteAlbum(id: number): void {
    this.albumState.setLoading(true);
    this.albumState.clearError();

    this.albumApi.delete(id).subscribe({
      next: () => {
        this.albumState.removeAlbum(id);
        this.albumState.setLoading(false);
        this.notification.success('Álbum excluído com sucesso!');
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError;
        const message = apiError?.message || error.message || 'Falha ao excluir álbum';

        this.albumState.setError(message);
        this.albumState.setLoading(false);
        this.notification.error(message);
      }
    });
  }

  /**
   * Clear selected album
   */
  clearSelectedAlbum(): void {
    this.albumState.setSelectedAlbum(null);
  }

  /**
   * Reset state
   */
  reset(): void {
    this.albumState.reset();
  }
}
