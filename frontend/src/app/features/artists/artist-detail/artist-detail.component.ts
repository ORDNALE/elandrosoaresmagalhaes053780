import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ArtistFacade, AuthFacade } from '@core/facades';
import { DialogService } from '@core/services/dialog.service';
import { ArtistaResponse } from '@core/models';

@Component({
  selector: 'app-artist-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './artist-detail.component.html'
})
export class ArtistDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly artistFacade = inject(ArtistFacade);
  private readonly authFacade = inject(AuthFacade);
  private readonly dialogService = inject(DialogService);

  readonly artist$ = this.artistFacade.selectedArtist$;
  readonly loading$ = this.artistFacade.loading$;
  readonly error$ = this.artistFacade.error$;

  ngOnInit(): void {
    const artistId = this.route.snapshot.paramMap.get('id');
    if (artistId) {
      this.loadArtist(Number(artistId));
    } else {
      this.router.navigate(['/artists']);
    }
  }

  loadArtist(id: number): void {
    this.artistFacade.loadArtistById(id);
  }

  async deleteArtist(artist: ArtistaResponse): Promise<void> {
    const confirmed = await this.dialogService.confirm({
      title: 'Excluir Artista',
      message: `Tem certeza que deseja excluir "${artist.nome}"?`,
      confirmText: 'Excluir',
      type: 'danger'
    });

    if (confirmed) {
      this.artistFacade.deleteArtist(artist.id, '/artists');
    }
  }

  get isAdmin(): boolean {
    return this.authFacade.isAdmin();
  }
}
