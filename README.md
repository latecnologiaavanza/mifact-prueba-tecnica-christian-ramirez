# Mantenimiento de Productos

Prueba técnica desarrollada por **Christian Ramirez**.

Aplicación de inventario desarrollada como prueba técnica. El proyecto está dividido en un frontend Angular y una API REST con Spring Boot, conectados a MySQL.

## Arquitectura

| Componente | Tecnología | Puerto local |
|---|---|---:|
| Frontend | Angular 18 standalone, Angular Material, Bootstrap Icons y SweetAlert2 | 4200 |
| Backend | Spring Boot 2.7, Java 8, Spring Data JPA y Flyway | 8081 |
| Base de datos | MySQL 8 | 3307 |

El frontend consume la API mediante `/api/v1/products`. En desarrollo, Angular utiliza `proxy.conf.json`; con Docker, Nginx reenvía las peticiones `/api` al contenedor del backend.

## Estructura del proyecto

```text
.
├── docker-compose.yml
├── inventory-api/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── inventory-web/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── package.json
│   └── src/
└── .github/workflows/
    ├── backend-ci.yml
    └── frontend-ci.yml
```

## Requisitos

Para ejecutar el proyecto completo con Docker solo necesitas:

- Docker Desktop con Docker Compose.

Para ejecutar los módulos por separado:

- Java 8 y Maven 3.5 o superior.
- Node.js 20 o superior y npm.
- MySQL 8 si el backend se ejecuta fuera de Docker.

## Ejecutar todo con Docker

Desde la raíz del repositorio:

```bash
docker compose up --build
```

Aplicación web:

```text
http://localhost:4200
```

API:

```text
http://localhost:8081
```

Para detener los contenedores:

```bash
docker compose down
```

La información de MySQL se conserva en el volumen `inventory_mysql_data`. Para eliminar también los datos locales, utiliza `docker compose down -v`.

## Ejecutar el frontend localmente

Con el backend disponible en `http://localhost:8081`:

```bash
cd inventory-web
npm ci
npm start
```

El frontend estará disponible en `http://localhost:4200`. El archivo `proxy.conf.json` redirige las peticiones `/api` hacia el backend local.

## Ejecutar el backend localmente

Desde `inventory-api`, configura las variables de conexión a MySQL y ejecuta:

```bash
mvn clean verify
mvn spring-boot:run
```

Flyway crea la tabla `products` y Hibernate valida el esquema sin modificarlo automáticamente.

## API REST

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/products` | Lista todos los productos |
| GET | `/api/v1/products?name=laptop` | Busca productos por nombre |
| GET | `/api/v1/products/{id}` | Obtiene un producto |
| POST | `/api/v1/products` | Crea un producto |
| PUT | `/api/v1/products/{id}` | Actualiza un producto |
| DELETE | `/api/v1/products/{id}` | Elimina un producto |

Documentación OpenAPI:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- Especificación: `http://localhost:8081/v3/api-docs`
- Health check: `http://localhost:8081/actuator/health`

## Rate limit

La API permite por defecto 100 solicitudes por dirección IP dentro de una ventana de 60 segundos. Al superar el límite responde con `429 Too Many Requests`.

Las variables configurables son:

```text
RATE_LIMIT_ENABLED=true
RATE_LIMIT_MAX_REQUESTS=100
RATE_LIMIT_WINDOW_SECONDS=60
```

## Pruebas y build

Backend:

```bash
cd inventory-api
mvn -B clean verify
```

Frontend:

```bash
cd inventory-web
npm ci
npx ng test --watch=false --browsers=ChromeHeadless
npm run build
```

## GitHub Actions

El repositorio contiene dos workflows:

- `backend-ci.yml`: ejecuta las pruebas Maven, genera el `.jar`, valida Docker Compose y construye la imagen del backend.
- `frontend-ci.yml`: instala dependencias, ejecuta las pruebas Angular, genera el build de producción y construye la imagen del frontend.

Se ejecutan en pushes a `main` o `develop` y en Pull Requests hacia `main`. Las imágenes Docker se validan en GitHub Actions, pero no se publican en Docker Hub; la persona evaluadora puede construirlas localmente con `docker compose up --build`.
