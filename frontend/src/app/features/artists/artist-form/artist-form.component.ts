import { Component, inject, OnInit, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ArtistFacade } from '@core/facades';
import { TipoArtista, ArtistaRequest } from '@core/models';

@Component({
  selector: 'app-artist-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './artist-form.component.html'
})
export class ArtistFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly artistFacade = inject(ArtistFacade);
  private readonly destroyRef = inject(DestroyRef);

  artistForm!: FormGroup;
  isEditMode = false;
  artistId?: number;

  readonly TipoArtista = TipoArtista;
  readonly loading$ = this.artistFacade.loading$;
  readonly error$ = this.artistFacade.error$;

  ngOnInit(): void {
    this.initForm();
    this.checkEditMode();
  }

  private initForm(): void {
    this.artistForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      tipo: [TipoArtista.SOLO, Validators.required]
    });
  }

  private checkEditMode(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.artistId = Number(id);
      this.loadArtist(this.artistId);
    }
  }

  private loadArtist(id: number): void {
    // Trigger load
    this.artistFacade.loadArtistById(id);

    // Subscribe to state changes to populate form
    this.artistFacade.selectedArtist$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(artist => {
        if (artist && artist.id === id) {
          this.artistForm.patchValue({
            nome: artist.nome,
            tipo: artist.tipo
          });
        }
      });
  }

  onSubmit(): void {
    if (this.artistForm.invalid) {
      this.artistForm.markAllAsTouched();
      return;
    }

    const request: ArtistaRequest = {
      nome: this.artistForm.value.nome.trim(),
      tipo: this.artistForm.value.tipo
    };

    if (this.isEditMode && this.artistId) {
      this.artistFacade.updateArtist(this.artistId, request);
    } else {
      this.artistFacade.createArtist(request);
    }
  }

  onCancel(): void {
    if (this.artistForm.dirty) {
      if (confirm('You have unsaved changes. Are you sure you want to cancel?')) {
        this.router.navigate(['/artists']);
      }
    } else {
      this.router.navigate(['/artists']);
    }
  }

  get nome() {
    return this.artistForm.get('nome');
  }

  get tipo() {
    return this.artistForm.get('tipo');
  }
}
