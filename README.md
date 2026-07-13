# Cloud File Storage

Pet-проект многопользовательского облачного хранилища файлов на Spring Boot (аналог Google Drive): регистрация, сессии, загрузка/скачивание файлов и папок, поиск и React UI.

## Description

Проект демонстрирует backend для файлового облака с разделением хранилищ по ответственности:

- пользователи и метаданные — PostgreSQL + Liquibase;
- файлы и папки — S3-совместимое хранилище MinIO;
- HTTP-сессии — Redis через Spring Session;
- REST API под общим префиксом `/api` + одностраничный frontend из статики Spring Boot.

Ключевые сценарии:

- регистрация, авторизация и logout на cookie-сессиях;
- CRUD-операции над ресурсами (файлы/папки): upload, download (zip для папок), move/rename, delete, search;
- работа с директориями: просмотр содержимого и создание пустых папок;
- изоляция данных пользователей в MinIO по префиксу `user-{id}-files/`.

## Stack

Основные технологии:

- Java 24
- Spring Boot 4.0.6
- Spring Security (session-based auth)
- Spring Session Data Redis
- Spring Data JPA
- Liquibase
- MinIO Java SDK 8.6.0
- MapStruct 1.5.5
- Lombok
- OpenAPI / Swagger (springdoc 3.0.2)
- JUnit + Testcontainers (PostgreSQL, Redis)

Инфраструктура:

- PostgreSQL 16
- Redis 7
- RedisInsight
- MinIO
- Docker / Docker Compose

## Pre-deployment requirements

- Java 24+
- Docker
- Docker Compose

## Fast start

Клонировать репозиторий:

```bash
git clone https://github.com/ChestnutLord/cloud-file-storage.git
cd cloud-file-storage
```

Создать `.env` из шаблона:

```bash
cp .env.example .env
```

Собрать приложение:

```bash
./gradlew clean bootJar
```

Запустить инфраструктуру и приложение:

```bash
docker compose up -d --build
```

Проверить UI и Swagger:

- Frontend: http://localhost:8080/
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## Main endpoints

| Сервис | URL |
|--------|-----|
| Frontend | http://localhost:8080/ |
| API | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| MinIO API | http://localhost:9000 |
| MinIO Console | http://localhost:9090 |
| RedisInsight | http://localhost:5540 |

### Auth / User

| Method | Path | Описание |
|--------|------|----------|
| POST | `/api/auth/sign-up` | Регистрация + создание сессии |
| POST | `/api/auth/sign-in` | Авторизация |
| POST | `/api/auth/sign-out` | Logout |
| GET | `/api/user/me` | Текущий пользователь |

### Resources / Directories

| Method | Path | Описание |
|--------|------|----------|
| GET | `/api/resource?path=` | Информация о ресурсе |
| DELETE | `/api/resource?path=` | Удаление ресурса |
| GET | `/api/resource/download?path=` | Скачивание файла или zip-папки |
| POST | `/api/resource/move?from=&to=` | Переименование / перемещение |
| GET | `/api/resource/search?query=` | Поиск по имени |
| POST | `/api/resource?path=` | Upload (multipart, поле `object`) |
| GET | `/api/directory?path=` | Содержимое папки |
| POST | `/api/directory?path=` | Создание пустой папки |

## Flow: Auth & sessions

Как работает авторизация:

1. `POST /api/auth/sign-up` или `POST /api/auth/sign-in` аутентифицирует пользователя через Spring Security.
2. `AuthService` сохраняет `SecurityContext` в HTTP-сессию.
3. Spring Session подменяет in-memory session и пишет данные в Redis (namespace `cloud-file-storage:session`).
4. Браузер получает cookie `JSESSIONID`; дальнейшие запросы к `/api/**` идут уже как authenticated.
5. `POST /api/auth/sign-out` инвалидирует сессию в Redis.

Пользователи хранятся в PostgreSQL (`users`), активные логины — в Redis.

## Flow: Files & folders (MinIO)

Как хранятся файлы:

1. При старте создаётся (или проверяется) bucket `user-files`.
2. Для каждого пользователя корень хранения — `user-{id}-files/`.
3. API-пути относительные (например `docs/file.txt`); префикс пользователя добавляется только во внутреннем слое и не протекает в REST-ответы.
4. Папки в S3 моделируются через directory marker; move папки = copy children + delete source.
5. Download папки отдаёт streaming zip без полной буферизации архива в памяти.

## Flow: Frontend

Как подключён UI:

1. Собранный React frontend лежит в `src/main/resources/static/`.
2. Spring раздаёт статику с корня (`/`); API остаётся под `/api`.
3. `SpaWebConfig` делает fallback на `index.html` для client-side routing.
4. Настройки фронта — в `static/config.js` (`baseApi: "/api"`).

## Development

Структура проекта (упрощённо):

```text
cloud-file-storage/
├── src/main/java/.../controller/   # REST + OpenAPI interfaces
├── src/main/java/.../service/      # бизнес-логика
├── src/main/java/.../repository/   # JPA + MinIO storage layer
├── src/main/resources/static/      # React frontend
├── src/main/resources/db/          # Liquibase migrations
├── docker-compose.yaml
├── Dockerfile
└── .env.example
```

### Конфигурация

Переменные окружения задаются в `.env`. См. `.env.example`.

Для Docker-сети app получает `REDIS_HOST=redis` из `docker-compose.yaml`.  
Если запускаете приложение локально (вне контейнера), а Redis в Docker — используйте `REDIS_HOST=localhost`.

### Тесты

```bash
./gradlew test
```

Интеграционные тесты auth поднимают PostgreSQL и Redis через Testcontainers.
