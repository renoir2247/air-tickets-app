# Спецификация интерфейсов между слоями

## Интерфейсы слоя Mediator (Service)

### `IBookingService`

```java
package com.aviacassa.service;

public interface IBookingService {
    Booking createBooking(Long flightId, List<PassengerDto> passengers);
    void cancelBooking(Long bookingId);
    List<Booking> findAll();
    Booking findById(Long bookingId);
}
```

| Метод | Описание |
|-------|----------|
| `createBooking(Long flightId, List<PassengerDto> passengers)` | Создаёт новое бронирование на указанный рейс для списка пассажиров (1–9 человек). Проверяет доступность мест, резервирует их на 15 минут и возвращает созданный объект `Booking` со статусом `PENDING`. |
| `cancelBooking(Long bookingId)` | Отменяет бронирование в статусе `PENDING`. Освобождает зарезервированные места на рейсе и переводит статус в `CANCELLED`. |
| `findAll()` | Возвращает список всех бронирований в системе. |
| `findById(Long bookingId)` | Возвращает бронирование по идентификатору. Выбрасывает исключение, если не найдено. |

### `IPaymentService`

```java
package com.aviacassa.service;

public interface IPaymentService {
    Payment initPayment(Long bookingId, PaymentMethod paymentMethod);
    void processWebhook(String transactionId, PaymentStatus status);
}
```

| Метод | Описание |
|-------|----------|
| `initPayment(Long bookingId, PaymentMethod paymentMethod)` | Инициализирует платёж для указанного бронирования. Создаёт объект `Payment` со статусом `PENDING` и рассчитанной суммой. |
| `processWebhook(String transactionId, PaymentStatus status)` | Обрабатывает callback от платёжного шлюза. При статусе `PAID` подтверждает бронирование, генерирует билеты и обновляет статус платежа. |

---

## Интерфейсы слоя Entity (Repository)

### `BookingRepository`

```java
package com.aviacassa.repository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime createdAt);
}
```

| Метод | Описание |
|-------|----------|
| `findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime createdAt)` | Находит бронирования с указанным статусом, созданные до заданной даты. Используется для фоновой отмены просроченных бронирований. |

### `FlightRepository`

```java
package com.aviacassa.repository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByFlightNumberAndDepartureTime(String flightNumber, LocalDateTime departureTime);
}
```

| Метод | Описание |
|-------|----------|
| `findByFlightNumberAndDepartureTime(String flightNumber, LocalDateTime departureTime)` | Находит рейс по номеру и времени вылета. Используется для проверки уникальности при загрузке данных из ГСБД. |

### `PaymentRepository`

```java
package com.aviacassa.repository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
}
```

| Метод | Описание |
|-------|----------|
| `findByTransactionId(String transactionId)` | Находит платёж по внешнему идентификатору транзакции от платёжного шлюза. |

### `UserRepository`

```java
package com.aviacassa.repository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

| Метод | Описание |
|-------|----------|
| `findByUsername(String username)` | Находит пользователя по логину. Используется при аутентификации для загрузки данных пользователя и проверки пароля. |

---

## Интерфейсы слоя Foundation (Mapper)

### `BookingMapper`

```java
@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingDTO toDto(Booking booking);
    List<BookingDTO> toDtoList(List<Booking> bookings);
    PassengerDTO toPassengerDto(Passenger passenger);
    List<PassengerDTO> toPassengerDtoList(List<Passenger> passengers);
    TicketDTO toTicketDto(Ticket ticket);
    List<TicketDTO> toTicketDtoList(List<Ticket> tickets);
    PassengerDto passengerRequestToPassengerDto(PassengerRequest request);
    List<PassengerDto> passengerRequestListToPassengerDtoList(List<PassengerRequest> requests);
}
```

### `FlightMapper`

```java
@Mapper(componentModel = "spring")
public interface FlightMapper {
    FlightDTO toDto(Flight flight);
    List<FlightDTO> toDtoList(List<Flight> flights);
}
```

### `PaymentMapper`

```java
@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentDTO toDto(Payment payment);
    List<PaymentDTO> toDtoList(List<Payment> payments);
}
```

---

## Интерфейсы REST API (Control ↔ Presentation)

| Метод | Endpoint | Описание | Авторизация |
|-------|----------|----------|-------------|
| POST | `/api/auth/login` | Аутентификация пользователя, возврат JWT | Нет |
| GET | `/api/flights` | Получить список всех рейсов | JWT |
| GET | `/api/flights/search` | Поиск рейсов по пунктам отправления/назначения | JWT |
| POST | `/api/bookings` | Создать бронирование | JWT |
| POST | `/api/bookings/{id}/cancel` | Отменить бронирование | JWT |
| GET | `/api/bookings` | Получить список всех бронирований | JWT |
| GET | `/api/bookings/{id}` | Получить детали бронирования | JWT |
| GET | `/api/bookings/{id}/status` | Получить статус бронирования | JWT |
| POST | `/api/payments` | Инициализировать оплату | JWT |
| POST | `/api/payments/webhook` | Callback от платёжного шлюза | Нет |
| GET | `/api/reports/sales` | Отчёт о продажах за период | JWT (ADMIN) |
