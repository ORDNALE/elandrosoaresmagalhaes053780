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
     * Lista artistas com paginação e filtros
     * @param pageRequest - Parâmetros de paginação (page, size)
     * @param filter - Filtros opcionais (nome, tipo, sort)
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
     * Obtém artista por ID com álbuns
     */
    getById(id: number): Observable<ArtistaResponse> {
        return this.http.get<ArtistaResponse>(`${this.baseUrl}/${id}`);
    }

    /**
     * POST /v1/artistas
     * Cria novo artista (apenas ADMIN)
     */
    create(request: ArtistaRequest): Observable<ArtistaResponse> {
        return this.http.post<ArtistaResponse>(this.baseUrl, request);
    }

    /**
     * PUT /v1/artistas/{id}
     * Atualiza artista (apenas ADMIN)
     */
    update(id: number, request: ArtistaRequest): Observable<void> {
        return this.http.put<void>(`${this.baseUrl}/${id}`, request);
    }

    /**
     * DELETE /v1/artistas/{id}
     * Deleta artista (apenas ADMIN)
     */
    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }
}
