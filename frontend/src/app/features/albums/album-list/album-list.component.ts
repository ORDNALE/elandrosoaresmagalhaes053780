import { Component, inject, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { AlbumFacade } from '@core/facades';
import { DialogService } from '@core/services/dialog.service';
import { WebSocketService, AlbumCreatedEvent } from '@core/services/websocket/websocket.service';
import { PaginationComponent } from '@shared/components/pagination/pagination.component';

@Component({
  selector: 'app-album-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PaginationComponent],
  templateUrl: './album-list.component.html',
  styleUrl: './album-list.component.scss'
})
export class AlbumListComponent implements OnInit, OnDestroy {
  private readonly albumFacade = inject(AlbumFacade);
  private readonly dialogService = inject(DialogService);
  private readonly wsService = inject(WebSocketService);
  private readonly destroy$ = new Subject<void>();

  // State observables
  readonly albums$ = this.albumFacade.albums$;
  readonly loading$ = this.albumFacade.loading$;
  readonly error$ = this.albumFacade.error$;
  readonly page$ = this.albumFacade.page$;
  readonly pageCount$ = this.albumFacade.pageCount$;
  readonly total$ = this.albumFacade.total$;

  // Pagination and filter state
  pageSize = signal(12);
  sortField = signal<'titulo'>('titulo');
  sortDirection = signal<'asc' | 'desc'>('asc');

  // WebSocket notification
  newAlbumNotification = signal<AlbumCreatedEvent | null>(null);
  showNotification = signal(false);

  // Filter form
  filterForm = new FormGroup({
    titulo: new FormControl(''),
    nomeArtista: new FormControl('')
  });

  ngOnInit(): void {
    this.loadAlbums();
    this.setupFilters();
    this.setupWebSocket();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupFilters(): void {
    this.filterForm.valueChanges
      .pipe(
        debounceTime(400),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.loadAlbums(0);
      });
  }

  private setupWebSocket(): void {
    // Listen for album creation events
    this.wsService.onAlbumCreated()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (event) => {
          console.log('New album created:', event);
          this.newAlbumNotification.set(event);
          this.showNotification.set(true);

          // Auto-hide notification after 5 seconds
          setTimeout(() => {
            this.showNotification.set(false);
          }, 5000);

          // Auto-refresh list
          this.loadAlbums();
        },
        error: (error) => {
          console.error('WebSocket error:', error);
        }
      });
  }

  loadAlbums(page?: number): void {
    const titulo = this.filterForm.value.titulo || undefined;
    const nomeArtista = this.filterForm.value.nomeArtista || undefined;
    const sort = `${this.sortField()},${this.sortDirection()}`;

    this.albumFacade.loadAlbums(
      { page: page ?? 0, size: this.pageSize() },
      { titulo, nomeArtista, sort }
    );
  }

  onPageChange(page: number): void {
    this.loadAlbums(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  onSortChange(field: 'titulo'): void {
    if (this.sortField() === field) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('asc');
    }
    this.loadAlbums();
  }

  clearFilters(): void {
    this.filterForm.reset({ titulo: '', nomeArtista: '' });
    this.loadAlbums(0);
  }

  async deleteAlbum(id: number, title: string): Promise<void> {
    const confirmed = await this.dialogService.confirm({
      title: 'Excluir Álbum',
      message: `Tem certeza que deseja excluir "${title}"?`,
      confirmText: 'Excluir',
      type: 'danger'
    });

    if (confirmed) {
      this.albumFacade.deleteAlbum(id);
    }
  }

  dismissNotification(): void {
    this.showNotification.set(false);
  }

  // Expose Math for template
  readonly Math = Math;
}
