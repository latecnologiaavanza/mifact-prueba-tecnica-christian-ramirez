# Inventory API

API REST para el mantenimiento de productos, desarrollada con Spring Boot y Java 8.

## Tecnologías

- Java 8 (SDK/JDK)
- Spring Boot 2.7.18
- Spring Web, Spring Data JPA y Bean Validation
- MySQL 8 y Flyway
- Swagger/OpenAPI
- Docker Compose

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/products` | Lista productos |
| GET | `/api/v1/products?name=laptop` | Busca por nombre |
| GET | `/api/v1/products/{id}` | Obtiene un producto |
| POST | `/api/v1/products` | Crea un producto |
| PUT | `/api/v1/products/{id}` | Actualiza un producto |
| DELETE | `/api/v1/products/{id}` | Elimina un producto |

Las respuestas de validación y errores se devuelven en español.

## Ejecutar con Docker

Requisitos: Docker y Docker Compose.

```bash
cd ..
docker compose up --build
```

La API queda disponible en `http://localhost:8081`.

## Ejecutar localmente

Requisitos: Java 8, Maven 3.5+ y MySQL 8 escuchando en el puerto `3307`. Copia `.env.example` como `.env`, ajusta tus credenciales y ejecuta:

```bash
mvn clean verify
mvn spring-boot:run
```

Flyway crea la tabla `products` al arrancar; Hibernate utiliza `ddl-auto: validate` para no modificar el esquema automáticamente.

## Pruebas

Las pruebas unitarias, de controlador, repositorio e integración usan H2 y no requieren levantar MySQL:

```bash
mvn clean verify
```

## Documentación y salud

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI: `http://localhost:8081/v3/api-docs`
- Health: `http://localhost:8081/actuator/health`
