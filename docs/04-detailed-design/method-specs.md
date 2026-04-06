# Спецификация методов

Сигнатуры и контракты ключевых методов по каждому слою архитектуры.

---

## Control — REST-контроллеры

### AuthController

```java
/**
 * POST /auth/login
 * Аутентификация кассира. Возвращает JWT-токен.
 */
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request);
```

### BookingController

```java
/**
 * POST /bookings
 * Создание нового бронирования с пассажирами.
 * @throws InsufficientSeatsException если мест на рейсе недостаточно
 */
@PostMapping
public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingRequest request);

/**
 * POST /bookings/{id}/cancel
 * Отмена бронирования. Освобождает места на рейсе.
 */
@PostMapping("/{id}/cancel")
public ResponseEntity<Void> cancelBooking(@PathVariable Long id);

/**
 * GET /bookings
 * Получение всех бронирований текущего пользователя.
 */
@GetMapping
public ResponseEntity<List<BookingDTO>> getAllBookings();

/**
 * GET /bookings/{id}
 * Получение деталей бронирования по ID.
 */
@GetMapping("/{id}")
public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id);

/**
 * GET /bookings/{id}/status
 * Получение статуса бронирования.
 */
@GetMapping("/{id}/status")
public ResponseEntity<BookingStatusDTO> getBookingStatus(@PathVariable Long id);
```

### FlightController

```java
/**
 * GET /flights
 * Получение списка всех доступных рейсов.
 */
@GetMapping
public ResponseEntity<List<FlightDTO>> getAllFlights();

/**
 * GET /flights/search
 * Поиск рейсов по пунктам отправления и назначения.
 */
@GetMapping("/search")
public ResponseEntity<List<FlightDTO>> searchFlights(
    @RequestParam(required = false) String origin,
    @RequestParam(required = false) String destination
);
```

### PaymentController

```java
/**
 * POST /payments
 * Инициализация платежа для бронирования.
 */
@PostMapping
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public ResponseEntity<PaymentDTO> initPayment(@Valid @RequestBody PaymentInitRequest request);

/**
 * POST /payments/webhook
 * Обработка вебхука от платёжной системы.
 */
@PostMapping("/webhook")
public ResponseEntity<Void> processWebhook(@Valid @RequestBody PaymentWebhookDTO request);
```

### ReportController

```java
/**
 * GET /reports/sales
 * Отчёт о продажах за период (только для ADMIN).
 */
@GetMapping("/sales")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ReportDTO> getSalesReport(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
);
```

---

## Mediator — сервисы бизнес-логики

### IBookingService

```java
public interface IBookingService {

    /**
     * Создание бронирования на выбранный рейс.
     * @throws InsufficientSeatsException если свободных мест меньше, чем пассажиров
     * @throws EntityNotFoundException если рейс не найден
     */
    Booking createBooking(Long flightId, List<PassengerDto> passengers);

    /**
     * Отмена бронирования. Освобождает места на рейсе.
     * @throws EntityNotFoundException если бронирование не найдено
     */
    void cancelBooking(Long bookingId);

    /**
     * Получение всех бронирований.
     */
    List<Booking> findAll();

    /**
     * Получение бронирования по ID.
     * @throws EntityNotFoundException если бронирование не найдено
     */
    Booking findById(Long bookingId);
}
```

### IPaymentService

```java
public interface IPaymentService {

    /**
     * Инициализация платежа для бронирования.
     * @throws EntityNotFoundException если бронирование не найдено
     * @throws PaymentFailedException если платёж не может быть создан
     */
    Payment initPayment(Long bookingId, PaymentMethod paymentMethod);

    /**
     * Обработка вебхука от платёжной системы.
     * Обновляет статус платежа и, при успехе, статус бронирования.
     */
    void processWebhook(String transactionId, PaymentStatus status);
}
```

---

## Foundation — репозитории

```java
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime createdAt);
}

public interface FlightRepository extends JpaRepository<Flight, Long> {
    Optional<Flight> findByFlightNumberAndDepartureTime(String flightNumber, LocalDateTime departureTime);
}

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
}

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

---

## Клиентская часть — React Components

### BookingForm

```typescript
/**
 * Многошаговая форма бронирования с динамическим списком пассажиров.
 * Валидация через React Hook Form + Zod.
 */
export const BookingForm: React.FC<BookingFormProps> = ({ selectedFlight }) => {
    const methods = useForm<BookingFormData>({
        resolver: zodResolver(bookingFormSchema),
        defaultValues: { flightId: selectedFlight.id, passengers: [defaultPassenger] },
        mode: 'onChange',
    });

    const onSubmit = async (data: BookingFormData) => {
        const booking = await createBooking({
            flightId: selectedFlight.id,
            passengers: data.passengers,
        });
        setCurrentBooking(booking);
        navigate(`/payment/${booking.id}`);
    };
};
```

### apiClient

```typescript
/**
 * Централизованный Axios-инстанс с JWT-инжекцией и обработкой 401.
 */
export const apiClient = axios.create({
    baseURL: '/api',
    headers: { 'Content-Type': 'application/json' },
});

// Request interceptor: добавляет Authorization: Bearer <token>
apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('aviacassa_jwt');
    if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// Response interceptor: при 401 очищает токен и разлогинивает
apiClient.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('aviacassa_jwt');
            window.dispatchEvent(new Event('auth:logout'));
        }
        return Promise.reject(error);
    }
);
```
