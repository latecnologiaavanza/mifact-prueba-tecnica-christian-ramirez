import { CommonModule, CurrencyPipe } from '@angular/common';
import {
  Component,
  DestroyRef,
  OnInit,
  inject
} from '@angular/core';
import {
  FormControl,
  ReactiveFormsModule
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  filter,
  finalize,
  map,
  merge,
  Observable,
  of,
  startWith,
  Subject,
  switchMap
} from 'rxjs';

import { NotificationService } from '../../../../core/services/notification.service';
import { Product } from '../../models/product.model';
import { ProductApiService } from '../../services/product-api.service';
import { ProductFormComponent } from '../../components/product-form/product-form.component';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatTooltipModule
  ],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css'
})
export class ProductListComponent implements OnInit {
  private readonly productService = inject(ProductApiService);
  private readonly dialog = inject(MatDialog);
  private readonly notification = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly refresh$ = new Subject<void>();

  readonly searchControl = new FormControl('', { nonNullable: true });
  readonly displayedColumns = ['name', 'description', 'quantity', 'price', 'actions'];

  products: Product[] = [];
  loading = false;
  deletingId: number | null = null;

  ngOnInit(): void {
    const search$ = this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    );

    const reload$ = this.refresh$.pipe(map(() => this.searchControl.value));

    merge(search$, reload$)
      .pipe(
        startWith(this.searchControl.value),
        switchMap((name) => this.loadProducts(name)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((products) => {
        this.products = products;
      });
  }

  openCreate(): void {
    this.dialog
      .open(ProductFormComponent, {
        width: '600px',
        maxWidth: '95vw',
        autoFocus: 'first-tabbable',
        data: {}
      })
      .afterClosed()
      .pipe(
        filter((saved) => saved === true),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.refresh$.next());
  }

  openEdit(product: Product): void {
    this.dialog
      .open(ProductFormComponent, {
        width: '600px',
        maxWidth: '95vw',
        autoFocus: 'first-tabbable',
        data: { product }
      })
      .afterClosed()
      .pipe(
        filter((saved) => saved === true),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.refresh$.next());
  }

  async confirmDelete(product: Product): Promise<void> {
    const confirmed = await this.notification.confirm(
      `¿Seguro que deseas eliminar “${product.name}”? Esta acción no se puede deshacer.`,
      'Eliminar producto'
    );

    if (!confirmed) {
      return;
    }

    this.deletingId = product.id;
    this.productService
      .delete(product.id)
      .pipe(
        finalize(() => (this.deletingId = null)),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => {
          this.notification.success('El producto fue eliminado correctamente.');
          this.refresh$.next();
        }
      });
  }

  private loadProducts(name: string): Observable<Product[]> {
    this.loading = true;

    return this.productService.getAll(name).pipe(
      catchError(() => of([])),
      finalize(() => (this.loading = false))
    );
  }
}
