import { Component, inject, OnInit, signal, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { lastValueFrom, takeUntil } from 'rxjs';
import { map } from 'rxjs/operators';
import { AlbumFacade, ArtistFacade } from '@core/facades';
import { AlbumRequest, AlbumResponse, ArtistaResponse } from '@core/models';

@Component({
  selector: 'app-album-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './album-form.component.html'
})
export class AlbumFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly albumFacade = inject(AlbumFacade);
  private readonly artistFacade = inject(ArtistFacade);
  // private readonly coverApiService = inject(CoverApiService); // Removed
  private readonly destroyRef = inject(DestroyRef);

  albumForm!: FormGroup;
  isEditMode = false;
  albumId?: number;

  // Available artists for selection
  availableArtists = signal<ArtistaResponse[]>([]);
  loadingArtists = signal(false);

  // Cover upload
  selectedFiles = signal<File[]>([]);
  filePreviewUrls = signal<string[]>([]);

  readonly loading$ = this.albumFacade.loading$;
  readonly error$ = this.albumFacade.error$;

  ngOnInit(): void {
    this.initForm();
    this.loadArtists();
    this.checkEditMode();
  }

  private initForm(): void {
    this.albumForm = this.fb.group({
      titulo: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(200)]],
      artistaIds: [[], [Validators.required, Validators.minLength(1)]]
    });
  }

  private loadArtists(): void {
    this.loadingArtists.set(true);
    // Load all artists for selection
    this.artistFacade.loadArtists({ page: 0, size: 1000 }, {});

    this.artistFacade.artists$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(artists => {
        this.availableArtists.set(artists);
        this.loadingArtists.set(false);
      });
  }

  private checkEditMode(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.albumId = Number(id);
      this.loadAlbum(this.albumId);
    } else {
      // Check for pre-selected artist from query params
      const artistId = this.route.snapshot.queryParamMap.get('artistId');
      if (artistId) {
        this.albumForm.patchValue({
          artistaIds: [Number(artistId)]
        });
      }
    }
  }

  private loadAlbum(id: number): void {
    // Trigger load
    this.albumFacade.loadAlbumById(id);

    // Subscribe to state
    this.albumFacade.selectedAlbum$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(album => {
        if (album && album.id === id) {
          this.albumForm.patchValue({
            titulo: album.titulo,
            artistaIds: album.artistas.map(a => a.id)
          });
        }
      });
  }


  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const files = Array.from(input.files);
      this.selectedFiles.set([...this.selectedFiles(), ...files]);

      // Create preview URLs
      const newUrls = files.map(file => URL.createObjectURL(file));
      this.filePreviewUrls.update(urls => [...urls, ...newUrls]);
    }
  }

  removeFile(index: number): void {
    // Revoke the object URL to free memory
    const url = this.filePreviewUrls()[index];
    if (url) {
      URL.revokeObjectURL(url);
    }

    const files = this.selectedFiles();
    files.splice(index, 1);
    this.selectedFiles.set([...files]);

    this.filePreviewUrls.update(urls => urls.filter((_, i) => i !== index));
  }

  toggleArtist(artistId: number): void {
    const currentIds = this.albumForm.value.artistaIds as number[];
    const index = currentIds.indexOf(artistId);

    if (index > -1) {
      currentIds.splice(index, 1);
    } else {
      currentIds.push(artistId);
    }

    this.albumForm.patchValue({ artistaIds: [...currentIds] });
  }

  isArtistSelected(artistId: number): boolean {
    const currentIds = this.albumForm.value.artistaIds as number[];
    return currentIds.includes(artistId);
  }

  onSubmit(): void {
    if (this.albumForm.invalid) {
      this.albumForm.markAllAsTouched();
      return;
    }

    const request: AlbumRequest = {
      titulo: this.albumForm.value.titulo.trim(),
      artistaIds: this.albumForm.value.artistaIds
    };

    const files = this.selectedFiles();

    if (this.isEditMode && this.albumId) {
      this.albumFacade.updateAlbum(this.albumId, request, files);
    } else {
      this.albumFacade.createAlbum(request, files);
    }
  }

  onCancel(): void {
    if (this.albumForm.dirty || this.selectedFiles().length > 0) {
      if (confirm('You have unsaved changes. Are you sure you want to cancel?')) {
        this.router.navigate(['/albums']);
      }
    } else {
      this.router.navigate(['/albums']);
    }
  }

  get titulo() {
    return this.albumForm.get('titulo');
  }

  get artistaIds() {
    return this.albumForm.get('artistaIds');
  }
}
