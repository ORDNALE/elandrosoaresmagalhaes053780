import { Injectable, inject } from '@angular/core';
import { ToastService } from '../../shared/services/toast.service';

/**
 * Serviço de Notificação
 * Serviço centralizado para notificações do usuário, aderindo aos requisitos do padrão Facade.
 * Envolve o ToastService para fornecer uma interface padronizada.
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
