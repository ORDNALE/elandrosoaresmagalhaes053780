import { Routes } from '@angular/router';
import { ArtistListComponent } from './artist-list/artist-list.component';
import { ArtistDetailComponent } from './artist-detail/artist-detail.component';

export const ARTISTS_ROUTES: Routes = [
  {
    path: '',
    component: ArtistListComponent
  },
  {
    path: 'new',
    loadComponent: () => import('./artist-form/artist-form.component').then(m => m.ArtistFormComponent)
  },
  {
    path: ':id',
    component: ArtistDetailComponent
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./artist-form/artist-form.component').then(m => m.ArtistFormComponent)
  }
];