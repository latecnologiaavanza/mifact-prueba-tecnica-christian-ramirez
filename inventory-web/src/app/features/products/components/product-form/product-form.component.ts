import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { finalize } from 'rxjs';

import { ApiError } from '../../models/api-error.model';
import { Product } from '../../models/product.model';
import { ProductRequest } from '../../models/product-request.model';
import { ProductApiService } from '../../services/product-api.service';
import { NotificationService } from '../../../../core/services/notification.service';

export interface ProductFormDialogData {
  product?: Product;
}

const nonBlankValidator: ValidatorFn = (
  control: AbstractControl
): ValidationErrors | null => {
  const value = typeof control.value === 'string' ? control.value.trim() : '';
  return value.length > 0 ? null : { blank: true };
};

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.css'
})
export class ProductFormComponent {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly productService = inject(ProductApiService);
  private readonly notification = inject(NotificationService);
  private readonly dialogRef = inject(MatDialogRef<ProductFormComponent, boolean>);
  readonly data = inject<ProductFormDialogData>(MAT_DIALOG_DATA);

  saving = false;

  readonly form = this.formBuilder.group({
    name: ['', [Validators.required, nonBlankValidator, Validators.maxLength(100)]],
    description: ['', [Validators.maxLength(500)]],
    quantity: [0, [Validators.required, Validators.min(0)]],
    price: [0, [Validators.required, Validators.min(0.01), Validators.pattern(/^\d{1,10}(\.\d{1,2})?$/)]]
  });

  constructor() {
    if (this.data.product) {
      this.form.patchValue({
        name: this.data.product.name,
        description: this.data.product.description ?? '',
        quantity: this.data.product.quantity,
        price: this.data.product.price
      });
    }
  }

  get isEditing(): boolean {
    return Boolean(this.data.product);
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const request: ProductRequest = {
      name: value.name.trim(),
      description: value.description.trim() || null,
      quantity: value.quantity,
      price: value.price
    };

    this.saving = true;

    const operation$ = this.data.product
      ? this.productService.update(this.data.product.id, request)
      : this.productService.create(request);

    operation$
      .pipe(finalize(() => (this.saving = false)))
      .subscribe({
        next: () => {
          this.dialogRef.close(true);
          this.notification.success(
            this.isEditing
              ? 'El producto fue actualizado correctamente.'
              : 'El producto fue creado correctamente.'
          );
        },
        error: (error: HttpErrorResponse) => this.applyServerErrors(error)
      });
  }

  cancel(): void {
    this.dialogRef.close(false);
  }

  serverError(controlName: 'name' | 'description' | 'quantity' | 'price'): string | null {
    return this.form.controls[controlName].getError('server') ?? null;
  }

  private applyServerErrors(error: HttpErrorResponse): void {
    if (error.status !== 400) {
      return;
    }

    const apiError = error.error as ApiError | undefined;
    Object.entries(apiError?.fieldErrors ?? {}).forEach(([field, message]) => {
      const control = this.form.get(field);
      if (control) {
        control.setErrors({ ...control.errors, server: message });
        control.markAsTouched();
      }
    });
  }
}
