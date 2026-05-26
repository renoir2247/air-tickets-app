# Этап 10: Развёртывание и администрирование

## Цель этапа

Обеспечение процесса развёртывания приложения «Авиакасса» с использованием Docker и Docker Compose.

## Результаты

- Конфигурация Docker Compose для всех сервисов
- Dockerfile для backend и frontend
- Инструкция по развёртыванию

---

## Требования к окружению

- **Docker** 24.0+
- **Docker Compose** 2.20+
- **Git** — для клонирования репозитория

---

## Архитектура контейнеров

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   nginx:alpine  │────▶│  Spring Boot    │────▶│  PostgreSQL 15  │
│  (frontend:80)  │     │ (backend:8080)  │     │   (db:5432)     │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                               │
                               ▼
                        ┌─────────────────┐
                        │   redis:7-alpine│
                        │   (redis:6379)  │
                        └─────────────────┘
```

---

## Docker Compose

Файл `docker-compose.yml` в корне проекта описывает 5 сервисов:

### `db` — PostgreSQL 15

- База данных `aviacassa`
- Пользователь `aviauser` / пароль `aviapass`
- Порт `5432:5432`
- Healthcheck через `pg_isready`

### `redis` — Redis 7

- Кэш и сессии
- Порт `6379:6379`

### `backend` — Spring Boot

- Собирается из `./backend/Dockerfile`
- Порт `8080:8080`
- Переменные окружения для подключения к БД и Redis
- Зависит от `db` (condition: healthy) и `redis`

### `frontend` — React + Nginx

- Собирается из `./frontend/Dockerfile`
- Порт `80:80`
- Nginx раздаёт статику из `/usr/share/nginx/html`
- Проксирует `/api` на backend

### `pgadmin` — pgAdmin 4

- Веб-интерфейс для управления БД
- Порт `5050:80`
- Email `admin@gmail.com` / пароль `adminpass`

---

## Dockerfile backend

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Dockerfile frontend

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

---

## Развёртывание

### Шаг 1: Клонирование репозитория

```bash
git clone <url>
cd air-tickets-app
```

### Шаг 2: Запуск всех сервисов

```bash
sudo docker-compose up --build
```

При первом запуске:
- Собираются образы backend и frontend
- PostgreSQL создаёт базу `aviacassa`
- Flyway применяет миграции
- Backend запускается на порту 8080
- Frontend доступен на порту 80

### Шаг 3: Проверка работоспособности

- **Frontend:** http://localhost
- **Backend API:** http://localhost:8080/api
- **pgAdmin:** http://localhost:5050

### Шаг 4: Остановка

```bash
sudo docker-compose down
```

Для полной очистки (включая volumes):

```bash
sudo docker-compose down -v
```

---

## Управление сервисами

### Пересборка отдельного сервиса

```bash
sudo docker-compose build --no-cache frontend
sudo docker-compose up -d frontend
```

### Просмотр логов

```bash
# Все сервисы
sudo docker-compose logs -f

# Только backend
sudo docker-compose logs -f backend
```

### Выполнение команд внутри контейнера

```bash
# Войти в контейнер backend
sudo docker-compose exec backend sh

# Войти в PostgreSQL
sudo docker-compose exec db psql -U aviauser -d aviacassa
```

---

## Резервное копирование базы данных

```bash
# Создание дампа
sudo docker-compose exec db pg_dump -U aviauser aviacassa > backup_$(date +%Y%m%d).sql

# Восстановление из дампа
cat backup.sql | sudo docker-compose exec -T db psql -U aviauser -d aviacassa
```

---

## Устранение неполадок

- **Порт 80 занят:** остановите программу, использующую порт 80, или измените mapping в `docker-compose.yml`
- **Порт 8080 занят:** аналогично для backend
- **База данных не поднимается:** проверьте healthcheck `pg_isready`
- **Frontend не собирается:** убедитесь, что `index.html` присутствует в `frontend/`
- **Ошибка CORS:** backend настроен через `CorsConfig.java` для разрешённых origin
