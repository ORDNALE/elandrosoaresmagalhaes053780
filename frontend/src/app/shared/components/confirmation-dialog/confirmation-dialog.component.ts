import { Component, HostListener, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogService } from '@core/services/dialog.service';



@Component({
  selector: 'app-confirmation-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirmation-dialog.component.html',
  styles: [`
    @keyframes scale-in {
      from {
        transform: scale(0.9);
        opacity: 0;
      }
      to {
        transform: scale(1);
        opacity: 1;
      }
    }

    .animate-scale-in {
      animation: scale-in 0.2s ease-out;
    }
  `]
})
export class ConfirmationDialogComponent {
  private readonly dialogService = inject(DialogService);

  // Bind signals from service
  readonly isOpen = this.dialogService.isOpen;
  readonly data = this.dialogService.data;

  onConfirm(): void {
    this.dialogService.close(true);
  }

  onCancel(): void {
    this.dialogService.close(false);
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.isOpen()) {
      this.onCancel();
    }
  }
}
