import { Routes } from '@angular/router';
import { authGuard } from '@core/guards';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.DASHBOARD_ROUTES)
  },
  {
    path: 'artists',
    canActivate: [authGuard],
    loadChildren: () => import('./features/artists/artist.routes').then(m => m.ARTISTS_ROUTES)
  },
  {
    path: 'albums',
    canActivate: [authGuard],
    loadChildren: () => import('./features/albums/albums.route').then(m => m.ALBUMS_ROUTES)
  },
  {
    path: '**',
    redirectTo: 'artists'
  }
];
