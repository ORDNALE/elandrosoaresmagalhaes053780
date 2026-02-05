import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { ArtistFacade, AuthFacade } from '@core/facades';
import { DialogService } from '@core/services/dialog.service';
import { TipoArtista } from '@core/models';
import { PaginationComponent } from '@shared/components/pagination/pagination.component';

@Component({
  selector: 'app-artist-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PaginationComponent],
  templateUrl: './artist-list.component.html'
})
export class ArtistListComponent implements OnInit {
  private readonly artistFacade = inject(ArtistFacade);
  private readonly authFacade = inject(AuthFacade);
  private readonly dialogService = inject(DialogService);

  readonly artists$ = this.artistFacade.artists$;
  readonly loading$ = this.artistFacade.loading$;
  readonly error$ = this.artistFacade.error$;
  readonly page$ = this.artistFacade.page$;
  readonly pageCount$ = this.artistFacade.pageCount$;
  readonly total$ = this.artistFacade.total$;

  pageSize = signal(10);
  sortField = signal<'nome' | 'tipo'>('nome');
  sortDirection = signal<'asc' | 'desc'>('asc');

  filterForm = new FormGroup({
    nome: new FormControl(''),
    tipo: new FormControl<TipoArtista | ''>('')
  });

  readonly TipoArtista = TipoArtista;

  ngOnInit(): void {
    this.loadArtists();
    this.setupFilters();
  }

  private setupFilters(): void {
    this.filterForm.valueChanges
      .pipe(
        debounceTime(400),
        distinctUntilChanged()
      )
      .subscribe(() => {
        this.loadArtists(0); // Reinicia para a primeira página ao filtrar
      });
  }

  loadArtists(page?: number): void {
    const nome = this.filterForm.value.nome || undefined;
    const tipo = this.filterForm.value.tipo || undefined;
    const sort = `${this.sortField()},${this.sortDirection()}`;

    this.artistFacade.loadArtists(
      { page: page ?? 0, size: this.pageSize() },
      { nome, tipo, sort }
    );
  }

  onPageChange(page: number): void {
    this.loadArtists(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  onSortChange(field: 'nome' | 'tipo'): void {
    if (this.sortField() === field) {
      // Toggle direction if same field
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDirection.set('asc');
    }
    this.loadArtists();
  }

  clearFilters(): void {
    this.filterForm.reset({ nome: '', tipo: '' });
    this.loadArtists(0);
  }

  async deleteArtist(id: number, name: string): Promise<void> {
    const confirmed = await this.dialogService.confirm({
      title: 'Excluir Artista',
      message: `Tem certeza que deseja excluir "${name}"?`,
      confirmText: 'Excluir',
      type: 'danger'
    });

    if (confirmed) {
      this.artistFacade.deleteArtist(id);
    }
  }

  get isAdmin(): boolean {
    return this.authFacade.isAdmin();
  }

  readonly Math = Math;
}
