// Artist Type Enum (matches backend)
export enum TipoArtista {
  SOLO = 'SOLO',
  BANDA = 'BANDA'
}

// Album Summary (nested in Artist response)
export interface AlbumResumoResponse {
  id: number;
  titulo: string;
}

// Artist Request (for POST/PUT)
export interface ArtistaRequest {
  nome: string;
  tipo: TipoArtista;
}

// Artist Summary (nested in Album response)
export interface ArtistaResumoResponse {
  id: number;
  nome: string;
  tipo: TipoArtista;
}

// Artist Full Response (GET by ID)
export interface ArtistaResponse {
  id: number;
  nome: string;
  tipo: TipoArtista;
  quantidadeAlbuns: number;
  albuns: AlbumResumoResponse[];
}

// Artist Filter for API queries
export interface ArtistaFilterRequest {
  nome?: string;
  tipo?: TipoArtista;
  sort?: string; // e.g., "nome,asc" or "nome,desc"
}

// Legacy compatibility (can be removed later)
/** @deprecated Use ArtistaResponse instead */
export type Artista = ArtistaResponse;
