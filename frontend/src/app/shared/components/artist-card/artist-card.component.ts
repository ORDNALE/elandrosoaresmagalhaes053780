import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ArtistaResponse } from '@core/models';

/**
 * Reusable artist card component for displaying artist information in a grid layout.
 *
 * @example
 * ```html
 * <app-artist-card
 *   [artist]="artist"
 *   [showActions]="true"
 *   (edit)="onEdit($event)"
 *   (delete)="onDelete($event)"
 * />
 * ```
 */
@Component({
  selector: 'app-artist-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './artist-card.component.html'
})
export class ArtistCardComponent {
  /** Artist data to display */
  artist = input.required<ArtistaResponse>();

  /** Whether to show action buttons */
  showActions = input(true);

  /** Emitted when edit button is clicked */
  edit = output<number>();

  /** Emitted when delete button is clicked */
  delete = output<number>();
}
