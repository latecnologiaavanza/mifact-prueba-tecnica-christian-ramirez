import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { ProductApiService } from './product-api.service';
import { Product } from '../models/product.model';

describe('ProductApiService', () => {
  let service: ProductApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ProductApiService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ProductApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should request products filtered by name', () => {
    const products: Product[] = [];

    service.getAll(' laptop ').subscribe((result) => {
      expect(result).toEqual(products);
    });

    const request = http.expectOne('/api/v1/products?name=laptop');
    expect(request.request.method).toBe('GET');
    request.flush(products);
  });
});
