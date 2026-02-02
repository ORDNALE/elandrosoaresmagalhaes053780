import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlbumResponse } from '@core/models';

/**
 * Reusable album card component for displaying album information in a grid layout.
 *
 * @example
 * ```html
 * <app-album-card
 *   [album]="album"
 *   [showActions]="true"
 *   (edit)="onEdit($event)"
 *   (delete)="onDelete($event)"
 * />
 * ```
 */
@Component({
  selector: 'app-album-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="group relative bg-[#0f0f13]/60 backdrop-blur-xl rounded-lg overflow-hidden border border-gray-700/50 hover:border-orange-500/50 transition-all duration-300 hover:shadow-[0_0_20px_rgba(249,115,22,0.3)]">

      <!-- Album Cover -->
      <div class="aspect-square bg-gradient-to-br from-purple-900/50 to-pink-900/50 flex items-center justify-center relative overflow-hidden">
        @if (album().capas && album().capas.length > 0) {
          <img
            [src]="album().capas[0].url"
            [alt]="album().titulo"
            class="w-full h-full object-cover"
            (error)="onImageError($event)"
          />
        } @else {
          <svg class="w-24 h-24 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zM9 10l12-3"></path>
          </svg>
        }
      </div>

      <!-- Album Info -->
      <div class="p-4">
        <h3 class="text-lg font-bold text-white mb-2 group-hover:text-orange-400 transition-colors truncate">
          {{ album().titulo }}
        </h3>
        <div class="space-y-1 mb-4">
          @for (artista of album().artistas; track artista.id) {
            <p class="text-sm text-gray-400 truncate">{{ artista.nome }}</p>
          }
        </div>

        <!-- Actions -->
        @if (showActions()) {
          <div class="flex gap-2">
            <button
              (click)="edit.emit(album().id)"
              class="flex-1 px-3 py-2 bg-orange-600/20 hover:bg-orange-600/30 text-orange-400 border border-orange-500/50 rounded text-center text-sm font-medium transition-all"
            >
              Edit
            </button>
            <button
              (click)="delete.emit(album().id)"
              class="px-3 py-2 bg-red-600/20 hover:bg-red-600/30 text-red-400 border border-red-500/50 rounded text-sm font-medium transition-all"
            >
              Delete
            </button>
          </div>
        }
      </div>
    </div>
  `
})
export class AlbumCardComponent {
  /** Album data to display */
  album = input.required<AlbumResponse>();

  /** Whether to show action buttons */
  showActions = input(true);

  /** Emitted when edit button is clicked */
  edit = output<number>();

  /** Emitted when delete button is clicked */
  delete = output<number>();

  /**
   * Handle image load errors by hiding the broken image
   */
  onImageError(event: Event): void {
    const target = event.target as HTMLImageElement;
    target.style.display = 'none';
  }
}
