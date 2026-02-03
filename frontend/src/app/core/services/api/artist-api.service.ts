import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import {
    ArtistaRequest,
    ArtistaResponse,
    ArtistaFilterRequest,
    PageRequest,
    Paged
} from '@core/models';

@Injectable({
    providedIn: 'root'
})
export class ArtistApiService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/v1/artistas`;

    /**
     * GET /v1/artistas
     * List artists with pagination and filters
     * @param pageRequest - Pagination params (page, size)
     * @param filter - Optional filters (nome, tipo, sort)
     */
    list(pageRequest: PageRequest, filter?: ArtistaFilterRequest): Observable<Paged<ArtistaResponse>> {
        let params = new HttpParams()
            .set('page', pageRequest.page.toString())
            .set('size', pageRequest.size.toString());

        if (filter?.nome) {
            params = params.set('nome', filter.nome);
        }
        if (filter?.tipo) {
            params = params.set('tipo', filter.tipo);
        }
        if (filter?.sort) {
            params = params.set('sort', filter.sort);
        }

        return this.http.get<Paged<ArtistaResponse>>(this.baseUrl, { params });
    }

    /**
     * GET /v1/artistas/{id}
     * Get artist by ID with albums
     */
    getById(id: number): Observable<ArtistaResponse> {
        return this.http.get<ArtistaResponse>(`${this.baseUrl}/${id}`);
    }

    /**
     * POST /v1/artistas
     * Create new artist (ADMIN only)
     */
    create(request: ArtistaRequest): Observable<ArtistaResponse> {
        return this.http.post<ArtistaResponse>(this.baseUrl, request);
    }

    /**
     * PUT /v1/artistas/{id}
     * Update artist (ADMIN only)
     */
    update(id: number, request: ArtistaRequest): Observable<void> {
        return this.http.put<void>(`${this.baseUrl}/${id}`, request);
    }

    /**
     * DELETE /v1/artistas/{id}
     * Delete artist (ADMIN only)
     */
    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }
}