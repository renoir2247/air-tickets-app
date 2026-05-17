# Слой доступа к данным (Foundation)

Репозитории реализованы через **Spring Data JPA** — интерфейсы, расширяющие `JpaRepository`. Spring автоматически генерирует реализацию по именам методов.

Каждый репозиторий отвечает ровно за одну таблицу. Бизнес-логика (проверки прав, транзакции) — строго в сервисном слое.

---

## BookingRepository

Отвечает за таблицу `bookings`. Используется `BookingService` для создания, отмены бронирований и отчётности.

```java
package com.aviacassa.repository;

import com.aviacassa.entity.Booking;
import com.aviacassa.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Поиск бронирований по статусу и дате создания.
     * Используется для очистки просроченных бронирований.
     */
    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime createdAt);
}
```

### Используемые методы

- `findByStatusAndCreatedAtBefore(status, createdAt)` — `BookingServiceImpl` / планировщик очистки просроченных бронирований
- `findById(id)` — `BookingServiceImpl.findById()`, `PaymentServiceImpl.initPayment()`
- `save(booking)` — `BookingServiceImpl.createBooking()`, `cancelBooking()`

---

## FlightRepository

Отвечает за таблицу `flights`. Все запросы на поиск рейсов и проверку доступности мест.

```java
package com.aviacassa.repository;

import com.aviacassa.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    /**
     * Поиск рейса по номеру и времени вылета.
     * Используется для проверки дублирования при создании рейсов.
     */
    Optional<Flight> findByFlightNumberAndDepartureTime(String flightNumber, LocalDateTime departureTime);
}
```

### Используемые методы

- `findById(id)` — `BookingServiceImpl.createBooking()` — проверка существования рейса и доступности мест
- `findByFlightNumberAndDepartureTime()` — валидация уникальности рейса
- `save(flight)` — обновление `availableSeats` при создании/отмене бронирования

---

## PaymentRepository

Отвечает за таблицу `payments`. Обработка вебхуков и поиск платежей по transaction ID.

```java
package com.aviacassa.repository;

import com.aviacassa.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Поиск платежа по внешнему идентификатору транзакции.
     * Используется при обработке вебхуков от платёжной системы.
     */
    Optional<Payment> findByTransactionId(String transactionId);
}
```

### Используемые методы

- `findByTransactionId(transactionId)` — `PaymentServiceImpl.processWebhook()`
- `save(payment)` — `PaymentServiceImpl.initPayment()`, `processWebhook()`

---

## UserRepository

Отвечает за таблицу `users`. Аутентификация и авторизация.

```java
package com.aviacassa.repository;

import com.aviacassa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по логину.
     * Используется при аутентификации через Spring Security.
     */
    Optional<User> findByUsername(String username);
}
```

### Используемые методы

- `findByUsername(username)` — `CustomUserDetailsService.loadUserByUsername()`
- `findById(id)` — загрузка пользователя по ID из JWT

---

## Архитектурные правила работы с репозиториями

- Репозитории — только интерфейсы, без `@Component` логики. Единственная ответственность: доступ к данным.
- Все запросы фильтруют по `userId` где применимо. Изоляция данных пользователей, безопасность.
- `findById` вместо прямого доступа — исключает IDOR-уязвимость.
- `FetchType.LAZY` вместо `EAGER` — контроль точек загрузки, устранение N+1.
- Каскадное удаление через `CascadeType.ALL` + `orphanRemoval = true` — гарантированная целостность.

---

## Взаимодействие репозиториев и сервисов

```
BookingController
       │
       ▼
BookingServiceImpl ──── BookingRepository ──── PostgreSQL: bookings
       │
       └──────────── FlightRepository    ──── PostgreSQL: flights
                              │
                              └── (decrement available_seats) при create

PaymentController
       │
       ▼
PaymentServiceImpl ───── PaymentRepository ──── PostgreSQL: payments
       │
       └──────────── BookingRepository    ──── PostgreSQL: bookings
                              │
                              └── обновление статуса при webhook

AuthController
       │
       ▼
CustomUserDetailsService ── UserRepository ──── PostgreSQL: users
```
