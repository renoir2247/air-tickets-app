# Этап 9: API документация

## Цель этапа

Документирование REST API серверной части приложения «Авиакасса».

## Результаты

- [Описание эндпоинтов](api-test.md)

---

## Базовый URL

```
http://localhost:8080/api
```

## Аутентификация

Все защищённые эндпоинты требуют заголовок:

```
Authorization: Bearer <jwt_token>
```

Токен получается через `POST /auth/login`.

---

## Общие эндпоинты

### Аутентификация

- `POST /auth/login` — вход в систему, возвращает JWT

### Рейсы

- `GET /flights` — список всех рейсов
- `GET /flights/search?origin=...&destination=...` — поиск рейсов

### Бронирования

- `POST /bookings` — создание бронирования
- `POST /bookings/{id}/cancel` — отмена бронирования
- `GET /bookings` — список бронирований
- `GET /bookings/{id}` — детали бронирования
- `GET /bookings/{id}/status` — статус бронирования

### Платежи

- `POST /payments` — инициализация платежа
- `POST /payments/webhook` — вебхук от платёжной системы

### Отчёты

- `GET /reports/sales?from=...&to=...` — отчёт о продажах (только ADMIN)
