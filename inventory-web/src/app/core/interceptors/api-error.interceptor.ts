import { inject } from '@angular/core';
import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

import { ApiError } from '../../features/products/models/api-error.model';
import { NotificationService } from '../services/notification.service';

export const apiErrorInterceptor: HttpInterceptorFn = (request, next) => {
  const notification = inject(NotificationService);

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      const apiError = error.error as ApiError | undefined;

      if (error.status === 0) {
        notification.error('No se pudo conectar con el servidor.');
      } else if (error.status === 400 && !apiError?.fieldErrors) {
        notification.error(apiError?.message ?? 'La solicitud no es válida.');
      } else if (error.status === 404) {
        notification.error(apiError?.message ?? 'El recurso solicitado no existe.');
      } else if (error.status === 409) {
        notification.error(apiError?.message ?? 'La operación genera un conflicto.');
      } else if (error.status === 429) {
        notification.error('Has realizado demasiadas solicitudes. Inténtalo nuevamente.');
      } else if (error.status >= 500) {
        notification.error('Ocurrió un error interno en el servidor.');
      }

      return throwError(() => error);
    })
  );
};
