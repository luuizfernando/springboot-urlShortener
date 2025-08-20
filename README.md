# URL Shortener

Spring Boot URL shortener with PostgreSQL, Docker, and Traefik. Supports short links like `https://short.local/{short_code}`, click metrics (IP, User-Agent), and CRUD endpoints.

## Endpoints

- **POST `/shorten`**
  Body:
  ```json
  { "url": "https://exemplo.com/minha-url" }
  ```
  Response:
  ```json
  { "short_url": "https://short.local/abc123", "original_url": "https://exemplo.com/minha-url" }
  ```

- **GET `/links`** → list of links with clicks
- **DELETE `/links/{short_code}`** → delete by code
- **GET `/{short_code}`** → 302 redirect to original URL and records click

## Run locally with Docker

1. Add host mapping for Traefik routing:
   - Windows: edit `C:\Windows\System32\drivers\etc\hosts` and add:
     ```
     127.0.0.1 short.local
     ```

2. Build and start services:
   ```bash
   docker compose up -d --build
   ```

3. Test:
   - API base: `http://short.local`
   - Traefik dashboard: `http://localhost:8081`

## Configuration

- App listens on `:8080` in container; Traefik exposes it on `:80` with rule `Host(\`short.local\`)`.
- Database: PostgreSQL `shortener/shortener` at `db:5432` (see `docker-compose.yml`).
- Hibernate `ddl-auto=update` (see `src/main/resources/application.yml`).

## Development (no Docker)

- Defaults to H2 in-memory DB. Run app:
  ```bash
  ./mvnw spring-boot:run
  ```
  Base URL: `http://localhost:8080`

