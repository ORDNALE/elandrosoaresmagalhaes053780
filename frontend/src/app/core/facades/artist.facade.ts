import { Injectable, inject } from '@angular/core';
import { Observable, tap, catchError, throwError, switchMap, map, finalize } from 'rxjs';
import { ArtistApiService } from '../services/api';
import { ArtistStateService } from '../state';
import {
  ArtistaRequest,
  ArtistaResponse,
  ArtistaFilterRequest,
  PageRequest,
  Paged,
  ApiError
} from '@core/models';
import { NotificationService } from '../services/notification.service';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

/**
 * Artist Facade - Orchestrates artist operations
 * Manages interaction between ArtistApiService and ArtistStateService
 */
@Injectable({
  providedIn: 'root'
})
export class ArtistFacade {
  private readonly artistApi = inject(ArtistApiService);
  private readonly artistState = inject(ArtistStateService);
  private readonly notification = inject(NotificationService);
  private readonly router = inject(Router);

  // Expose state observables
  readonly artists$ = this.artistState.artistList$;
  readonly selectedArtist$ = this.artistState.selectedArtist;
  readonly loading$ = this.artistState.isLoading$;
  readonly error$ = this.artistState.error$;
  readonly page$ = this.artistState.page$;
  readonly pageCount$ = this.artistState.pageCount$;
  readonly total$ = this.artistState.total$;

  /**
   * Load artists list with pagination and filters
   */
  loadArtists(pageRequest: PageRequest, filter?: ArtistaFilterRequest): void {
    this.artistState.setLoading(true);
    this.artistState.clearError();

    this.artistApi.list(pageRequest, filter)
      .pipe(finalize(() => this.artistState.setLoading(false)))
      .subscribe({
        next: (response) => {
          this.artistState.setArtists(response.content);
          this.artistState.setPagination(
            response.page,
            response.pageCount,
            response.total
          );
        },
        error: (error: HttpErrorResponse) => {
          const apiError = error.error as ApiError;
          const message = apiError?.message || error.message || 'Falha ao carregar artistas';

          this.artistState.setError(message);
          this.notification.error(message);
        }
      });
  }

  /**
   * Load artist by ID
   */
  loadArtistById(id: number): void {
    this.artistState.setLoading(true);
    this.artistState.clearError();

    this.artistApi.getById(id)
      .pipe(finalize(() => this.artistState.setLoading(false)))
      .subscribe({
        next: (artist) => {
          this.artistState.setSelectedArtist(artist);
        },
        error: (error: HttpErrorResponse) => {
          const apiError = error.error as ApiError;
          const message = apiError?.message || error.message || 'Falha ao carregar artista';

          this.artistState.setError(message);
          this.notification.error(message);
          this.router.navigate(['/artists']);
        }
      });
  }

  /**
   * Create new artist
   */
  createArtist(request: ArtistaRequest): void {
    this.artistState.setLoading(true);
    this.artistState.clearError();

    this.artistApi.create(request)
      .pipe(finalize(() => this.artistState.setLoading(false)))
      .subscribe({
        next: (artist) => {
          this.artistState.addArtist(artist);
          this.notification.success('Artista criado com sucesso!');
          this.router.navigate(['/artists']);
        },
        error: (error: HttpErrorResponse) => {
          const apiError = error.error as ApiError;
          const message = apiError?.message || error.message || 'Falha ao criar artista';

          this.artistState.setError(message);
          this.notification.error(message);
        }
      });
  }

  /**
   * Update artist
   */
  updateArtist(id: number, request: ArtistaRequest): void {
    this.artistState.setLoading(true);
    this.artistState.clearError();

    this.artistApi.update(id, request).pipe(
      switchMap(() => this.artistApi.getById(id)),
      finalize(() => this.artistState.setLoading(false))
    ).subscribe({
      next: (artist) => {
        this.artistState.updateArtist(id, artist);
        this.artistState.setSelectedArtist(artist);
        this.notification.success('Artista atualizado com sucesso!');
        this.router.navigate(['/artists']);
      },
      error: (error: HttpErrorResponse) => {
        const apiError = error.error as ApiError;
        const message = apiError?.message || error.message || 'Falha ao atualizar artista';

        this.artistState.setError(message);
        this.notification.error(message);
      }
    });
  }

  /**
   * Delete artist
   */
  deleteArtist(id: number, redirectTo?: string): void {
    this.artistState.setLoading(true);
    this.artistState.clearError();

    this.artistApi.delete(id)
      .pipe(finalize(() => this.artistState.setLoading(false)))
      .subscribe({
        next: () => {
          this.artistState.removeArtist(id);
          this.notification.success('Artista excluído com sucesso!');
          if (redirectTo) {
            this.router.navigate([redirectTo]);
          }
        },
        error: (error: HttpErrorResponse) => {
          const apiError = error.error as ApiError;
          const message = apiError?.message || error.message || 'Falha ao excluir artista';

          this.artistState.setError(message);
          this.notification.error(message);
        }
      });
  }

  /**
   * Clear selected artist
   */
  clearSelectedArtist(): void {
    this.artistState.setSelectedArtist(null);
  }

  /**
   * Reset state
   */
  reset(): void {
    this.artistState.reset();
  }
}
