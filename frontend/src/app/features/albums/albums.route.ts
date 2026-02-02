import { Routes } from '@angular/router';

export const ALBUMS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./album-list/album-list.component').then(m => m.AlbumListComponent)
  },
  {
    path: 'new',
    loadComponent: () => import('./album-form/album-form.component').then(m => m.AlbumFormComponent)
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./album-form/album-form.component').then(m => m.AlbumFormComponent)
  }
];
