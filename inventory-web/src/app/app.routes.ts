import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'products'
  },
  {
    path: 'products',
    loadComponent: () =>
      import('./features/products/pages/product-list/product-list.component')
        .then((module) => module.ProductListComponent)
  },
  {
    path: '**',
    redirectTo: 'products'
  }
];
