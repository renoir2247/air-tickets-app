# План тестирования

## Модульное тестирование (Unit-тесты)

### AuthControllerTest

- `login_WithValidCredentials_ShouldReturnToken` — вход с валидными данными возвращает JWT
- `login_WithInvalidCredentials_ShouldReturn401` — неверные учётные данные → 401

### BookingControllerTest

- `createBooking_WithValidData_ShouldReturn201` — создание бронирования
- `createBooking_WithInsufficientSeats_ShouldReturn409` — недостаточно мест → 409
- `cancelBooking_WithValidId_ShouldReturn200` — отмена бронирования
- `getAllBookings_ShouldReturnList` — список бронирований
- `getBookingById_WithValidId_ShouldReturnBooking` — поиск по ID

### FlightControllerTest

- `getAllFlights_ShouldReturnList` — список рейсов
- `searchFlights_WithParams_ShouldReturnFilteredList` — поиск с фильтрами

### PaymentControllerTest

- `initPayment_WithValidData_ShouldReturnPaymentDto` — инициализация платежа
- `processWebhook_WithValidData_ShouldReturn200` — обработка вебхука

### ReportControllerTest

- `getSalesReport_WithAdminRole_ShouldReturnReport` — отчёт для администратора
- `getSalesReport_WithUserRole_ShouldReturn403` — доступ запрещён для USER

### BookingServiceImplTest

- `createBooking_WithValidData_ShouldReturnBooking` — создание бронирования
- `createBooking_WithFlightNotFound_ShouldThrowException` — рейс не найден
- `createBooking_WithInsufficientSeats_ShouldThrowException` — недостаточно мест
- `cancelBooking_WithValidId_ShouldRestoreSeats` — отмена и восстановление мест
- `findAll_ShouldReturnAllBookings` — получение всех бронирований
- `findById_WithValidId_ShouldReturnBooking` — поиск по ID
- `findById_WithInvalidId_ShouldThrowException` — бронирование не найдено

### PaymentServiceImplTest

- `initPayment_WithValidData_ShouldReturnPayment` — создание платежа
- `initPayment_WithBookingNotFound_ShouldThrowException` — бронирование не найдено
- `processWebhook_WithPaidStatus_ShouldConfirmBooking` — подтверждение бронирования
- `processWebhook_WithFailedStatus_ShouldNotConfirmBooking` — платёж не прошёл

### CustomUserDetailsServiceTest

- `loadUserByUsername_WithExistingUser_ShouldReturnUserDetails` — загрузка пользователя
- `loadUserByUsername_WithNonExistingUser_ShouldThrowException` — пользователь не найден

### JwtServiceTest

- `generateToken_ShouldReturnToken` — генерация токена
- `isTokenValid_WithValidToken_ShouldReturnTrue` — валидация корректного токена
- `extractUsername_ShouldReturnCorrectUsername` — извлечение username
- `isTokenExpired_WithExpiredToken_ShouldReturnFalse` — проверка срока действия

### JwtAuthenticationFilterTest

- `doFilterInternal_WithValidToken_ShouldAuthenticate` — валидный токен аутентифицирует
- `doFilterInternal_WithInvalidToken_ShouldNotAuthenticate` — невалидный токен

### CorsConfigTest

- `corsConfigurer_ShouldReturnNonNullBean` — CORS-конфигурация создана

### SecurityConfigTest

- `filterChain_ShouldReturnSecurityFilterChain` — цепочка фильтров настроена
- `passwordEncoder_ShouldReturnBCrypt` — используется BCrypt

### GlobalExceptionHandlerTest

- `handleInsufficientSeats_ShouldReturn409` — обработка недостатка мест
- `handlePaymentFailed_ShouldReturn402` — обработка ошибки платежа
- `handleValidationException_ShouldReturn400` — обработка валидации
- `handleAuthenticationException_ShouldReturn401` — обработка аутентификации
- `handleGenericException_ShouldReturn500` — обработка неизвестной ошибки

### DtoAndEntityTest

- `bookingDto_Builder_ShouldCreateObject` — создание DTO через Builder
- `passengerRequest_Validation_ShouldPass` — валидация запроса пассажира

### PasswordTest

- `passwordEncoder_ShouldHashPassword` — хеширование пароля
- `passwordEncoder_ShouldMatchRawPassword` — проверка совпадения паролей

---

## Запуск тестов

```bash
cd backend
mvn test
```

Все тестовые файлы расположены в `backend/src/test/java/com/aviacassa/`.
