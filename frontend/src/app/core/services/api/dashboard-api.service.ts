import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { DashboardResponse } from '@core/models';

@Injectable({
    providedIn: 'root'
})
export class DashboardApiService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/v1/dashboard`;

    /**
     * GET /v1/dashboard
     * Obtém totais do dashboard (artistas, álbuns, novidades)
     */
    getTotals(): Observable<DashboardResponse> {
        return this.http.get<DashboardResponse>(this.baseUrl);
    }
}
