import { Routes } from '@angular/router';

export const ARTISTS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./artist-list/artist-list.component').then(m => m.ArtistListComponent)
  },
  {
    path: 'new',
    loadComponent: () => import('./artist-form/artist-form.component').then(m => m.ArtistFormComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./artist-detail/artist-detail.component').then(m => m.ArtistDetailComponent)
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./artist-form/artist-form.component').then(m => m.ArtistFormComponent)
  }
];
