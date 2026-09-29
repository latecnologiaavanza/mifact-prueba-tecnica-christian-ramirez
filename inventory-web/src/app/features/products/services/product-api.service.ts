import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Product } from '../models/product.model';
import { ProductRequest } from '../models/product-request.model';

@Injectable({ providedIn: 'root' })
export class ProductApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/products';

  getAll(name?: string): Observable<Product[]> {
    let params = new HttpParams();
    const normalizedName = name?.trim();

    if (normalizedName) {
      params = params.set('name', normalizedName);
    }

    return this.http.get<Product[]>(this.apiUrl, { params });
  }

  create(request: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, request);
  }

  update(id: number, request: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
