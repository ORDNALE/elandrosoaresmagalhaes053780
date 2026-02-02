import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AlbumResponse } from '@core/models';

@Injectable({
  providedIn: 'root'
})
export class AlbumStateService {
  private readonly albums$ = new BehaviorSubject<AlbumResponse[]>([]);
  private readonly selectedAlbum$ = new BehaviorSubject<AlbumResponse | null>(null);
  private readonly loading$ = new BehaviorSubject<boolean>(false);
  private readonly errorSubject$ = new BehaviorSubject<string | null>(null);

  // Pagination state
  private readonly pageSubject = new BehaviorSubject<number>(0);
  private readonly pageCountSubject = new BehaviorSubject<number>(0);
  private readonly totalSubject = new BehaviorSubject<number>(0);

  // Public observables
  readonly albumList$: Observable<AlbumResponse[]> = this.albums$.asObservable();
  readonly selectedAlbum: Observable<AlbumResponse | null> = this.selectedAlbum$.asObservable();
  readonly isLoading$: Observable<boolean> = this.loading$.asObservable();
  readonly error$: Observable<string | null> = this.errorSubject$.asObservable();
  readonly page$: Observable<number> = this.pageSubject.asObservable();
  readonly pageCount$: Observable<number> = this.pageCountSubject.asObservable();
  readonly total$: Observable<number> = this.totalSubject.asObservable();

  setAlbums(albums: AlbumResponse[]): void {
    this.albums$.next(albums);
  }

  setSelectedAlbum(album: AlbumResponse | null): void {
    this.selectedAlbum$.next(album);
  }

  addAlbum(album: AlbumResponse): void {
    const current = this.albums$.value;
    this.albums$.next([...current, album]);
  }

  updateAlbum(id: number, updated: AlbumResponse): void {
    const current = this.albums$.value;
    const index = current.findIndex(a => a.id === id);
    if (index !== -1) {
      current[index] = updated;
      this.albums$.next([...current]);
    }
  }

  removeAlbum(id: number): void {
    const current = this.albums$.value;
    this.albums$.next(current.filter(a => a.id !== id));
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
    this.albums$.next([]);
    this.selectedAlbum$.next(null);
    this.loading$.next(false);
    this.errorSubject$.next(null);
    this.pageSubject.next(0);
    this.pageCountSubject.next(0);
    this.totalSubject.next(0);
  }
}
