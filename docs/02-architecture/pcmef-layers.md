# Описание слоёв PCMEF

## Таблица слоёв

| Слой | Назначение | Технологии | Пакеты / Модули |
|------|-----------|------------|-----------------|
| Presentation | Отображение UI, обработка пользовательского ввода, клиентская маршрутизация | React 18, TypeScript, Tailwind CSS, Zustand | `frontend/src/pages/`, `frontend/src/components/`, `frontend/src/store/` |
| Control | Приём HTTP-запросов, валидация входных данных, формирование ответов | Spring Boot, Spring Web, `@RestController` | `com.aviacassa.controller` |
| Mediator | Бизнес-логика, управление транзакциями, координация слоёв | Spring Boot, `@Service`, `@Transactional` | `com.aviacassa.service` |
| Entity | Доменные объекты и доступ к данным | Spring Data JPA, Hibernate, PostgreSQL | `com.aviacassa.entity`, `com.aviacassa.repository` |
| Foundation | Маппинг, безопасность, миграции, конфигурация | MapStruct, Spring Security, Flyway, JWT | `com.aviacassa.mapper`, `com.aviacassa.config`, `com.aviacassa.exception` |

## Правила зависимостей

1. **Направленность сверху вниз.** Каждый слой может зависеть только от слоёв, расположенных ниже. Обратные зависимости запрещены.
2. **Интерфейсы в Mediator.** Слой Control взаимодействует с Mediator только через интерфейсы (`IBookingService`, `IPaymentService`), что позволяет подменять реализации при тестировании.
3. **Entity не зависит от Mediator.** Репозитории и сущности не содержат ссылок на сервисы. Бизнес-правила, требующие координации нескольких сущностей, реализуются в Mediator.
4. **Foundation — самый нижний слой.** Мапперы, конфигурации безопасности и обработка исключений используются всеми верхними слоями, но сами не зависят от них.
5. **Presentation изолирован.** Фронтенд-клиент не имеет прямого доступа к Entity или Foundation. Все взаимодействия происходят через REST API слоя Control.

## Распределение компонентов реального проекта

### Presentation

| Компонент | Назначение |
|-----------|------------|
| `pages/FlightsPage.tsx` | Поиск и отображение списка рейсов |
| `pages/BookingPage.tsx` | Форма оформления бронирования |
| `pages/PaymentPage.tsx` | Экран оплаты и статуса платежа |
| `pages/ReportsPage.tsx` | Просмотр отчётов (ADMIN) |
| `store/authStore.ts` | Хранение JWT и данных пользователя |
| `store/bookingStore.ts` | Управление состоянием бронирования |
| `api/client.ts` | Axios-инстанс с интерцепторами |

### Control

| Компонент | Назначение |
|-----------|------------|
| `AuthController` | Аутентификация (`/auth/login`) |
| `FlightController` | Поиск рейсов (`/flights`, `/flights/search`) |
| `BookingController` | CRUD бронирований (`/bookings/*`) |
| `PaymentController` | Инициализация оплаты и вебхук (`/payments/*`) |
| `ReportController` | Отчёты о продажах (`/reports/sales`) |

### Mediator

| Компонент | Назначение |
|-----------|------------|
| `IBookingService` / `BookingServiceImpl` | Создание/отмена бронирования, проверка лимитов, управление таймером |
| `IPaymentService` / `PaymentServiceImpl` | Инициализация платежа, обработка вебхука, генерация билетов |

### Entity

| Компонент | Назначение |
|-----------|------------|
| `Booking`, `Flight`, `Passenger`, `Payment`, `Ticket`, `User` | JPA-сущности с аннотациями `@Entity` |
| `BookingRepository` | Поиск бронирований по статусу и дате (для автоотмены) |
| `FlightRepository` | Поиск рейса по номеру и времени вылета |
| `PaymentRepository` | Поиск платежа по внешнему transactionId |
| `UserRepository` | Поиск пользователя по username |

### Foundation

| Компонент | Назначение |
|-----------|------------|
| `BookingMapper`, `FlightMapper`, `PaymentMapper` | MapStruct-мапперы Entity ↔ DTO |
| `SecurityConfig` | Конфигурация CORS, CSRF, фильтров JWT |
| `JwtService` | Генерация и валидация JWT-токенов |
| `GlobalExceptionHandler` | Централизованная обработка исключений |
| `Flyway` миграции V1–V3 | Версионирование схемы PostgreSQL |
