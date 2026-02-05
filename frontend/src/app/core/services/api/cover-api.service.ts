import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { CapaAlbumResponse } from '@core/models';

@Injectable({
    providedIn: 'root'
})
export class CoverApiService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/v1/albuns`;

    /**
     * POST /v1/albuns/{albumId}/capas
     * Upload cover images (multipart/form-data) - ADMIN only
     * @param albumId - Album ID
     * @param files - Array of image files
     */
    upload(albumId: number, files: File[]): Observable<CapaAlbumResponse[]> {
        const formData = new FormData();
        files.forEach(file => {
            formData.append('images', file, file.name);
        });

        return this.http.post<CapaAlbumResponse[]>(
            `${this.baseUrl}/${albumId}/capas`,
            formData
        );
    }

    /**
     * GET /v1/albuns/{albumId}/capas
     * List all covers for an album
     */
    listByAlbum(albumId: number): Observable<CapaAlbumResponse[]> {
        return this.http.get<CapaAlbumResponse[]>(`${this.baseUrl}/${albumId}/capas`);
    }

    /**
     * GET /v1/albuns/{albumId}/capas/{capaId}
     * Get specific cover URL (pre-signed URL from MinIO)
     */
    getById(albumId: number, capaId: number): Observable<CapaAlbumResponse> {
        return this.http.get<CapaAlbumResponse>(`${this.baseUrl}/${albumId}/capas/${capaId}`);
    }

    /**
     * DELETE /v1/albuns/{albumId}/capas/{capaId}
     * Delete cover (ADMIN only)
     */
    delete(albumId: number, capaId: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${albumId}/capas/${capaId}`);
    }
}
