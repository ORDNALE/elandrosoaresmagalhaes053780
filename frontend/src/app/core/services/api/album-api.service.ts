import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import {
    AlbumRequest,
    AlbumResponse,
    AlbumFilterRequest,
    PageRequest,
    Paged
} from '@core/models';

@Injectable({
    providedIn: 'root'
})
export class AlbumApiService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/v1/albuns`;

    /**
     * GET /v1/albuns
     * List albums with pagination and filters
     * @param pageRequest - Pagination params (page, size)
     * @param filter - Optional filters (nomeArtista, tipo, sort)
     */
    list(pageRequest: PageRequest, filter?: AlbumFilterRequest): Observable<Paged<AlbumResponse>> {
        let params = new HttpParams()
            .set('page', pageRequest.page.toString())
            .set('size', pageRequest.size.toString());

        if (filter?.titulo) {
            params = params.set('tituloAlbum', filter.titulo);
        }

        if (filter?.nomeArtista) {
            params = params.set('nomeArtista', filter.nomeArtista);
        }
        if (filter?.tipo) {
            params = params.set('tipo', filter.tipo);
        }
        if (filter?.sort) {
            params = params.set('sort', filter.sort);
        }

        return this.http.get<Paged<AlbumResponse>>(this.baseUrl, { params });
    }

    /**
     * GET /v1/albuns/{id}
     * Get album by ID with artists and covers
     */
    getById(id: number): Observable<AlbumResponse> {
        return this.http.get<AlbumResponse>(`${this.baseUrl}/${id}`);
    }

    /**
     * POST /v1/albuns
     * Create new album (ADMIN only)
     */
    create(request: AlbumRequest): Observable<AlbumResponse> {
        return this.http.post<AlbumResponse>(this.baseUrl, request);
    }

    /**
     * PUT /v1/albuns/{id}
     * Update album (ADMIN only)
     */
    update(id: number, request: AlbumRequest): Observable<void> {
        return this.http.put<void>(`${this.baseUrl}/${id}`, request);
    }

    /**
     * DELETE /v1/albuns/{id}
     * Delete album (ADMIN only)
     */
    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }
}