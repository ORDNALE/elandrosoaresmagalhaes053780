import { Injectable, inject, signal } from '@angular/core';
import { DashboardApiService } from '../services/api/dashboard-api.service'; // Adjust import path as needed, assume it's roughly here or via barrels if I update them
// Actually I better check imports in other facades. They use '../services/api'.
import { DashboardResponse } from '@core/models';
import { finalize } from 'rxjs';
import { NotificationService } from '../services/notification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '@core/models';

@Injectable({
    providedIn: 'root'
})
export class DashboardFacade {
    private readonly dashboardApi = inject(DashboardApiService);
    private readonly notification = inject(NotificationService); // Use logic from other facades

    // State signals
    readonly stats = signal<DashboardResponse | null>(null);
    readonly isLoading = signal<boolean>(false);
    readonly error = signal<string | null>(null);

    loadStats(): void {
        this.isLoading.set(true);
        this.error.set(null);

        this.dashboardApi.getTotals()
            .pipe(finalize(() => this.isLoading.set(false)))
            .subscribe({
                next: (response) => {
                    this.stats.set(response);
                },
                error: (error: HttpErrorResponse) => {
                    const apiError = error.error as ApiError;
                    const message = apiError?.message || error.message || 'Falha ao carregar dashboard';
                    this.error.set(message);
                    this.notification.error(message);
                }
            });
    }
}
