import { Injectable, inject } from '@angular/core';
import { ToastService } from '../../shared/services/toast.service';

/**
 * Notification Service
 * Centralized service for user notifications, adhering to the Facade pattern requirements.
 * Wraps ToastService to provide a standardized interface for Facades.
 */
@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private readonly toastService = inject(ToastService);

    success(message: string): void {
        this.toastService.success(message);
    }

    error(message: string): void {
        this.toastService.error('Error', message);
    }

    info(message: string): void {
        this.toastService.info(message);
    }

    warning(message: string): void {
        this.toastService.warning(message);
    }
}