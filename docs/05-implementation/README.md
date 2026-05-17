# Этап 5: Реализация ядра

## Цель этапа

Полная реализация слоёв Entity и Foundation серверной части, реализация ключевых Use Cases в Mediator (Service), модульное тестирование с покрытием > 40%.

## Результаты

- [Entity-слои и JPA-сущности](core-entities.md)
- [Сервисный слой (Mediator)](services.md)
- [Слой доступа к данным (Foundation)](repositories.md)
- [Модульное тестирование и покрытие](test-coverage.md)

---

## Структура серверного проекта

```
aviacassa-backend/
├── src/main/java/com/aviacassa/
│   ├── config/
│   │   ├── CorsConfig.java              ← CORS для Vite dev-сервер
│   │   ├── JwtAuthenticationFilter.java ← JWT-фильтр запросов
│   │   ├── JwtService.java              ← Генерация и валидация JWT
│   │   └── SecurityConfig.java          ← Spring Security + RBAC
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── BookingController.java
│   │   ├── FlightController.java
│   │   ├── PaymentController.java
│   │   └── ReportController.java
│   ├── service/
│   │   ├── IBookingService.java
│   │   ├── IPaymentService.java
│   │   ├── CustomUserDetailsService.java
│   │   └── impl/
│   │       ├── BookingServiceImpl.java
│   │       └── PaymentServiceImpl.java
│   ├── repository/
│   │   ├── BookingRepository.java
│   │   ├── FlightRepository.java
│   │   ├── PaymentRepository.java
│   │   └── UserRepository.java
│   ├── entity/
│   │   ├── Booking.java
│   │   ├── Flight.java
│   │   ├── Passenger.java
│   │   ├── Payment.java
│   │   ├── Ticket.java
│   │   ├── User.java
│   │   └── enums/
│   │       ├── BookingStatus.java
│   │       ├── FlightStatus.java
│   │       ├── PaymentMethod.java
│   │       ├── PaymentStatus.java
│   │       ├── Role.java
│   │       └── TicketStatus.java
│   ├── dto/
│   │   ├── AuthResponse.java
│   │   ├── BookingDTO.java
│   │   ├── BookingRequest.java
│   │   ├── FlightDTO.java
│   │   ├── LoginRequest.java
│   │   ├── PassengerDto.java
│   │   ├── PaymentDTO.java
│   │   └── ...
│   ├── mapper/
│   │   ├── BookingMapper.java           ← MapStruct
│   │   ├── FlightMapper.java
│   │   └── PaymentMapper.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       ├── InsufficientSeatsException.java
│       ├── PaymentFailedException.java
│       └── ValidationException.java
├── src/main/resources/
│   └── db/migration/                    ← Flyway миграции
│       ├── V1__init_schema.sql
│       ├── V2__add_users_table.sql
│       └── V3__seed_flights.sql
└── src/test/java/com/aviacassa/
    ├── config/
    │   ├── CorsConfigTest.java
    │   ├── JwtAuthenticationFilterTest.java
    │   ├── JwtServiceTest.java
    │   └── SecurityConfigTest.java
    ├── controller/
    │   ├── AuthControllerTest.java
    │   ├── BookingControllerTest.java
    │   ├── FlightControllerTest.java
    │   ├── PaymentControllerTest.java
    │   └── ReportControllerTest.java
    ├── service/
    │   ├── CustomUserDetailsServiceTest.java
    │   └── impl/
    │       ├── BookingServiceImplTest.java
    │       └── PaymentServiceImplTest.java
    ├── exception/
    │   └── GlobalExceptionHandlerTest.java
    ├── DtoAndEntityTest.java
    └── PasswordTest.java
```

## Выполненные требования

- **Серверная часть на Java (Spring Boot 3.2.5)** — реализована
- **REST API (10+ эндпоинтов)** — реализовано
- **Аутентификация через JWT (jjwt 0.12.5)** — реализована
- **База данных PostgreSQL 15 + Flyway миграции** — реализована
- **Модульное тестирование (покрытие ~40%)** — реализовано
- **React SPA (Vite + TypeScript)** — реализовано
- **Docker-контейнеризация** — реализована
