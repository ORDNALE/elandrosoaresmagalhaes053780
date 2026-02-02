import { ArtistaResumoResponse } from './artist.model';

// Album Request (for POST/PUT)
export interface AlbumRequest {
  titulo: string;
  artistaIds: number[];
}

// Cover/Capa Response
export interface CapaAlbumResponse {
  id: number;
  nomeArquivo: string;
  url: string;
}

// Album Full Response (GET by ID)
export interface AlbumResponse {
  id: number;
  titulo: string;
  artistas: ArtistaResumoResponse[];
  capas: CapaAlbumResponse[];
}

// Album Filter for API queries
export interface AlbumFilterRequest {
  titulo?: string;
  nomeArtista?: string;
  tipo?: string;
  sort?: string; // e.g., "titulo,asc" or "titulo,desc"
}
