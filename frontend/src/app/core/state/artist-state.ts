import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ArtistaResponse } from '@core/models';

@Injectable({
  providedIn: 'root'
})
export class ArtistStateService {
  private readonly artists$ = new BehaviorSubject<ArtistaResponse[]>([]);
  private readonly selectedArtist$ = new BehaviorSubject<ArtistaResponse | null>(null);
  private readonly loading$ = new BehaviorSubject<boolean>(false);
  private readonly errorSubject$ = new BehaviorSubject<string | null>(null);

  // Pagination state
  private readonly pageSubject = new BehaviorSubject<number>(0);
  private readonly pageCountSubject = new BehaviorSubject<number>(0);
  private readonly totalSubject = new BehaviorSubject<number>(0);

  // Public observables
  readonly artistList$: Observable<ArtistaResponse[]> = this.artists$.asObservable();
  readonly selectedArtist: Observable<ArtistaResponse | null> = this.selectedArtist$.asObservable();
  readonly isLoading$: Observable<boolean> = this.loading$.asObservable();
  readonly error$: Observable<string | null> = this.errorSubject$.asObservable();
  readonly page$: Observable<number> = this.pageSubject.asObservable();
  readonly pageCount$: Observable<number> = this.pageCountSubject.asObservable();
  readonly total$: Observable<number> = this.totalSubject.asObservable();

  setArtists(artists: ArtistaResponse[]): void {
    this.artists$.next(artists);
  }

  setSelectedArtist(artist: ArtistaResponse | null): void {
    this.selectedArtist$.next(artist);
  }

  addArtist(artist: ArtistaResponse): void {
    const current = this.artists$.value;
    this.artists$.next([...current, artist]);
  }

  updateArtist(id: number, updated: ArtistaResponse): void {
    const current = this.artists$.value;
    const index = current.findIndex(a => a.id === id);
    if (index !== -1) {
      current[index] = updated;
      this.artists$.next([...current]);
    }
  }

  removeArtist(id: number): void {
    const current = this.artists$.value;
    this.artists$.next(current.filter(a => a.id !== id));
  }

  setLoading(loading: boolean): void {
    this.loading$.next(loading);
  }

  setError(error: string | null): void {
    this.errorSubject$.next(error);
  }

  clearError(): void {
    this.errorSubject$.next(null);
  }

  setPagination(page: number, pageCount: number, total: number): void {
    this.pageSubject.next(page);
    this.pageCountSubject.next(pageCount);
    this.totalSubject.next(total);
  }

  reset(): void {
    this.artists$.next([]);
    this.selectedArtist$.next(null);
    this.loading$.next(false);
    this.errorSubject$.next(null);
    this.pageSubject.next(0);
    this.pageCountSubject.next(0);
    this.totalSubject.next(0);
  }
}
