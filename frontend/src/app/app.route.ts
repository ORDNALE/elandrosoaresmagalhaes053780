import { Routes } from '@angular/router';
import { authGuard } from '@core/guards';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'artists',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'artists',
    canActivate: [authGuard],
    loadChildren: () => import('./features/artists/artists.routes').then(m => m.ARTISTS_ROUTES)
  },
  {
    path: 'albums',
    canActivate: [authGuard],
    loadChildren: () => import('./features/albums/albums.routes').then(m => m.ALBUMS_ROUTES)
  },
  {
    path: '**',
    redirectTo: 'artists'
  }
];
