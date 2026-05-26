# Тестирование API

## Основные эндпоинты

### Аутентификация

- `POST /auth/login` — Вход в систему. Доступ: PUBLIC. Тело: `{ "username": "...", "password": "..." }`. Ответ: `{ "token": "..." }`.

### Рейсы

- `GET /flights` — Список всех рейсов. Доступ: USER, ADMIN.
- `GET /flights/search?origin=...&destination=...` — Поиск рейсов по маршруту. Доступ: USER, ADMIN.

### Бронирования

- `POST /bookings` — Создание бронирования. Доступ: USER, ADMIN. Тело: `{ "flightId": 1, "passengers": [...] }`.
- `POST /bookings/{id}/cancel` — Отмена бронирования. Доступ: USER, ADMIN.
- `GET /bookings` — Список бронирований. Доступ: USER, ADMIN.
- `GET /bookings/{id}` — Детали бронирования. Доступ: USER, ADMIN.
- `GET /bookings/{id}/status` — Статус бронирования. Доступ: USER, ADMIN.

### Платежи

- `POST /payments` — Инициализация платежа. Доступ: USER, ADMIN. Тело: `{ "bookingId": 1, "paymentMethod": "CREDIT_CARD" }`.
- `POST /payments/webhook` — Обработка вебхука. Доступ: PUBLIC. Тело: `{ "transactionId": "...", "status": "PAID" }`.

### Отчёты

- `GET /reports/sales?from=YYYY-MM-DD&to=YYYY-MM-DD` — Отчёт о продажах за период. Доступ: ADMIN.

---

## Примеры запросов

### Аутентификация

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"cashier1","password":"password"}'
```

### Поиск рейсов

```bash
curl -X GET "http://localhost:8080/api/flights/search?origin=Москва&destination=Сочи" \
  -H "Authorization: Bearer <token>"
```

### Создание бронирования

```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "flightId": 1,
    "passengers": [
      {
        "firstName": "Иван",
        "lastName": "Иванов",
        "passportNumber": "1234567890",
        "dateOfBirth": "1990-01-01"
      }
    ]
  }'
```

### Инициализация платежа

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"bookingId": 1, "paymentMethod": "CREDIT_CARD"}'
```

### Отчёт о продажах (ADMIN)

```bash
curl -X GET "http://localhost:8080/api/reports/sales?from=2026-05-01&to=2026-05-31" \
  -H "Authorization: Bearer <admin_token>"
```

---

## Коды ответов

- `200 OK` — успешное выполнение
- `201 Created` — ресурс создан
- `400 Bad Request` — ошибка валидации входных данных
- `401 Unauthorized` — отсутствует или невалидный JWT-токен
- `403 Forbidden` — недостаточно прав (например, USER пытается получить ADMIN-отчёт)
- `404 Not Found` — ресурс не найден
- `409 Conflict` — конфликт бизнес-правил (например, недостаточно мест)
- `500 Internal Server Error` — внутренняя ошибка сервера
