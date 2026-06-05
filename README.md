# Веб-приложение для бронирования авиабилетов «АвиаКасса»

**Автор:** Чекалин Егор Юрьевич  
**Группа:** ПИЖ-б-о-23-2  
**Траектория:** Web  
**Дата начала:** 03.03.2026  
**Дата сдачи:** 29.05.2026

---

## Описание проекта

**АвиаКасса** — это полнофункциональное веб-приложение для поиска и бронирования авиабилетов. Пользователи могут искать авиарейсы по направлениям и датам, просматривать детали перелётов, бронировать билеты с выбором мест, а также управлять своими бронированиями через личный кабинет. Администраторы имеют доступ к панели управления для добавления рейсов, просмотра отчётов и управления системой.

### Ключевые возможности

- **Поиск авиарейсов** — фильтрация по городам вылета/прилёта, датам, цене
- **Бронирование билетов** — выбор рейса, добавление пассажиров, выбор мест
- **Оплата** — интеграция с платёжной системой (sandbox-режим)
- **Личный кабинет** — история бронирований, управление профилем
- **Административная панель** — управление рейсами, отчёты по продажам
- **JWT-аутентификация** — безопасная регистрация и вход

---

## Траектория выполнения

- [ ] Мобильная разработка (Android + Spring Boot)
- [ ] Десктоп
- [x] Веб-разработка
- [ ] Enterprise

---

## Технологический стек

| Компонент            | Технология                                               |
|----------------------|----------------------------------------------------------|
| **Бэкенд**           | Java 17, Spring Boot 3.2, PostgreSQL 15, JPA/Hibernate   |
| **Веб-клиент**       | React 18, TypeScript 5.4, Vite 5.2, Tailwind CSS 3.4     |
| **Управление состоянием** | Zustand 4.5                                         |
| **API**              | REST (Axios), OpenAPI 3.0 (Swagger)                      |
| **Безопасность**     | JWT (jjwt 0.12.5), Spring Security, BCrypt               |
| **Платежи**          | Интеграция с платёжной системой (sandbox)                |
| **Миграции БД**      | Flyway                                                   |
| **Контейнеризация**  | Docker, Docker Compose                                   |
| **Сборка**           | Maven (backend), npm (frontend)                          |
| **Тестирование**     | JUnit 5, Mockito, JaCoCo                                 |
| **Инструменты**      | Git, Postman, VS Code, IntelliJ IDEA                     |

---

## Требования к окружению

| Требование               | Версия               |
|--------------------------|----------------------|
| Java JDK                 | 17+                  |
| Node.js                  | 18+                  |
| PostgreSQL               | 15+                  |
| Maven                    | 3.8+                 |
| Docker + Docker Compose  | 2.20+                |
| Git                      | 2.30+                |

---

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/lookitsssonya/course-project.git
cd course-project
```

### 2. Запуск через Docker Compose (рекомендуется)

```bash
docker-compose up --build
```

Поднимутся все сервисы:
- **PostgreSQL** — `localhost:5432`
- **Redis** — `localhost:6379`
- **Backend** — `http://localhost:8080`
- **Frontend** — `http://localhost:80`
- **PgAdmin** — `http://localhost:5050`

### 3. Локальный запуск бэкенда

```bash
cd backend
mvn spring-boot:run
```

Сервер запустится на `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 4. Локальный запуск фронтенда

```bash
cd frontend
npm install
npm run dev
```

Клиент запустится на `http://localhost:5173`

> **Примечание:** Для корректной работы фронтенда необходим запущенный бэкенд.

---

## API Endpoints

Базовый URL: `http://localhost:8080/api`

### Аутентификация

| Метод  | Эндпоинт                    | Описание              | Доступ |
|--------|-----------------------------|-----------------------|--------|
| POST   | `/api/auth/register`        | Регистрация           | PUBLIC |
| POST   | `/api/auth/login`           | Вход в систему        | PUBLIC |
| GET    | `/api/auth/me`              | Профиль пользователя  | USER   |
| PUT    | `/api/auth/profile`         | Обновление профиля    | USER   |
| PUT    | `/api/auth/change-password` | Смена пароля          | USER   |

### Авиарейсы

| Метод  | Эндпоинт                    | Описание                        | Доступ |
|--------|-----------------------------|---------------------------------|--------|
| GET    | `/api/flights`              | Список всех рейсов              | PUBLIC |
| GET    | `/api/flights/{id}`         | Детали рейса                    | PUBLIC |
| GET    | `/api/flights/search`       | Поиск по направлениям и датам   | PUBLIC |
| POST   | `/api/admin/flights`        | Добавление рейса                | ADMIN  |
| PUT    | `/api/admin/flights/{id}`   | Редактирование рейса            | ADMIN  |
| DELETE | `/api/admin/flights/{id}`   | Удаление рейса                  | ADMIN  |

### Бронирования

| Метод  | Эндпоинт                         | Описание                  | Доступ |
|--------|----------------------------------|---------------------------|--------|
| POST   | `/api/bookings`                  | Создание бронирования     | USER   |
| GET    | `/api/bookings/user`             | Бронирования пользователя | USER   |
| GET    | `/api/bookings/{id}`             | Детали бронирования       | USER   |
| PUT    | `/api/bookings/{id}/cancel`      | Отмена бронирования       | USER   |
| PUT    | `/api/bookings/{id}/confirm`     | Подтверждение бронирования| USER   |
| GET    | `/api/admin/bookings`            | Все бронирования          | ADMIN  |

### Платежи

| Метод  | Эндпоинт                         | Описание              | Доступ |
|--------|----------------------------------|-----------------------|--------|
| POST   | `/api/payments/create`           | Создание платежа      | USER   |
| GET    | `/api/payments/status/{id}`      | Статус платежа        | USER   |

### Отчёты (админ)

| Метод  | Эндпоинт                         | Описание              | Доступ |
|--------|----------------------------------|-----------------------|--------|
| GET    | `/api/reports/sales`             | Отчёт по продажам     | ADMIN  |
| GET    | `/api/reports/flights`           | Отчёт по рейсам       | ADMIN  |

Полная документация API: [Swagger UI](http://localhost:8080/swagger-ui.html)

---

## Структура документации

Вся документация находится в папке [`docs/`](docs/):

| Папка | Артефакты |
|-------|-----------|
| [`00-project-charter/`](docs/00-project-charter/) | Паспорт проекта, IDEF0, BUC, модель бизнес-классов, SWOT, ROI, глоссарий |
| [`01-requirements/`](docs/01-requirements/) | Use Case диаграмма, спецификации прецедентов, domain model, трассировка требований |
| [`02-architecture/`](docs/02-architecture/) | PCMEF-диаграмма, ADR, спецификация интерфейсов, диаграмма зависимостей |
| [`03-database/`](docs/03-database/) | ER-диаграмма, DDL-скрипты, ORM-стратегия |
| [`04-detailed-design/`](docs/04-detailed-design/) | Диаграммы последовательностей, спецификации методов, диаграмма классов |
| [`05-implementation/`](docs/05-implementation/) | Структура проекта, реализация слоёв, паттерны GoF |
| [`06-testing/`](docs/06-testing/) | Тест-планы, отчёт JaCoCo |
| [`07-refactoring/`](docs/07-refactoring/) | Запахи кода, Data Mapper, Identity Map, Lazy Load |
| [`08-ui/`](docs/08-ui/) | Скриншоты экранов приложения |
| [`09-api/`](docs/09-api/) | Эндпоинты, OpenAPI, curl-примеры |
| [`10-deployment/`](docs/10-deployment/) | Руководство по развёртыванию Docker Compose |
| [`11-user-guide/`](docs/11-user-guide/) | Руководство пользователя, руководство администратора |
| [`12-project-management/`](docs/12-project-management/) | WBS, диаграмма Ганта, оценка COCOMO |

---

## Архитектура (PCMEF)

Система построена на архитектурном паттерне **PCMEF** (Presentation-Control-Mediator-Entity-Foundation).

### Распределение слоёв

| Слой                 | Backend (Spring Boot)                              | Frontend (React)                           |
|----------------------|----------------------------------------------------|--------------------------------------------|
| **Presentation (P)** | —                                                  | React-компоненты, страницы (Tailwind CSS)  |
| **Control (C)**      | REST-контроллеры (`BookingController`, `FlightController`, `AuthController`, `PaymentController`, `ReportController`) | React Router, Zustand store, API-хуки      |
| **Mediator (M)**     | Сервисы (`BookingService`, `FlightService`, `AuthService`, `PaymentService`, `ReportService`) | —                                          |
| **Entity (E)**       | JPA-сущности (`Flight`, `Booking`, `Passenger`, `User`, `Ticket`, `Payment`) | TypeScript-интерфейсы, DTO-модели          |
| **Foundation (F)**   | Spring Data JPA репозитории, Flyway миграции       | Axios-клиент (`api/`), localStorage        |

### Ключевые ADR

| ADR     | Решение                                              |
|---------|------------------------------------------------------|
| ADR-001 | Клиент-серверная архитектура (React + Spring Boot)   |
| ADR-002 | PostgreSQL как основная СУБД                         |
| ADR-003 | JWT для stateless-аутентификации                     |
| ADR-004 | Flyway для версионирования схемы БД                  |
| ADR-005 | Docker Compose для оркестрации окружения             |

Подробнее: [`docs/02-architecture/adr.md`](docs/02-architecture/adr.md)

---

## Структура проекта

```
course-project/
├── backend/                    # Spring Boot приложение
│   ├── src/main/java/com/aviacassa/
│   │   ├── config/             # Security, JWT, CORS
│   │   ├── controller/         # REST-контроллеры
│   │   ├── service/            # Интерфейсы сервисов
│   │   ├── service/impl/       # Реализации сервисов
│   │   ├── repository/         # Spring Data JPA
│   │   ├── entity/             # JPA-сущности
│   │   ├── dto/                # Data Transfer Objects
│   │   ├── mapper/             # MapStruct мапперы
│   │   └── exception/          # Кастомные исключения
│   ├── src/main/resources/
│   │   ├── db/migration/       # Flyway миграции
│   │   └── application.yml
│   └── pom.xml
├── frontend/                   # React + Vite приложение
│   ├── src/
│   │   ├── api/                # Axios-клиенты
│   │   ├── components/         # React-компоненты
│   │   ├── pages/              # Страницы приложения
│   │   ├── store/              # Zustand store
│   │   ├── hooks/              # Кастомные хуки
│   │   ├── schemas/            # Zod-схемы валидации
│   │   └── config/             # Конфигурация
│   ├── index.html
│   └── package.json
├── docs/                       # Проектная документация
├── docker-compose.yml
└── README.md
```

---

## Статистика разработки

### Git метрики

| Метрика                   | Значение                |
|---------------------------|-------------------------|
| Всего коммитов            | 29                      |
| Период разработки         | 03.03.2026 – 29.05.2026 |
| Backend (Java)            | 67 файлов, ~3 000 SLOC  |
| Frontend (TS/TSX)         | 27 файлов, ~1 900 SLOC  |
| Покрытие тестами (JaCoCo) | ~40%                    |

---

## Авторы

**Чекалин Е.Ю.** — разработчик, документация  
Группа ПИЖ-б-о-23-2, email: imya2305@gmail.com, GitHub: renoir2247

---

## Лицензия

MIT License  
Этот проект распространяется под лицензией MIT.

---

## Полезные ссылки

- [Репозиторий проекта](https://github.com/lookitsssonya/course-project.git)
- [Документация (docs/)](docs/)
- [Swagger UI](http://localhost:8080/swagger-ui.html)
