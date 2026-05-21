# Этап 7: Рефакторинг и качество

## Цель этапа

Улучшить качество кода по результатам статического анализа. Внедрить обязательные паттерны рефакторинга и обновить тесты.

## Результаты

- Применены паттерны Data Mapper, Identity Map, Lazy Load
- Проведён рефакторинг согласно принципам SOLID

---

## Обязательные паттерны

- **Data Mapper** — реализован через MapStruct (`BookingMapper`, `FlightMapper`, `PaymentMapper`). Отображение Entity ↔ DTO вынесено в отдельные интерфейсы, что устраняет смешивание слоёв.
- **Identity Map** — реализован неявно через JPA first-level cache (`EntityManager`). При повторном запросе одного и того же объекта по ID Hibernate возвращает уже загруженный экземпляр из persistence context.
- **Lazy Load** — применён через `FetchType.LAZY` во всех ассоциациях `@OneToMany` и `@ManyToOne`. Коллекции загружаются только при явном обращении.

---

## Проведённые рефакторинги

### Внедрение MapStruct (Data Mapper)

До рефакторинга преобразование Entity → DTO выполнялось вручную в сервисах. После рефакторинга логика маппинга вынесена в отдельные интерфейсы:

```java
@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "flight.id", target = "flightId")
    @Mapping(source = "flight.flightNumber", target = "flightNumber")
    @Mapping(target = "passengerCount", expression = "java(booking.getPassengers().size())")
    BookingDTO toDto(Booking booking);
}
```

### Внедрение Zustand вместо React Context

До рефакторинга состояние аутентификации передавалось через React Context + useReducer. После рефакторинга используется Zustand с persist-мидлварой:

- Упрощён доступ к состоянию из любого компонента без провайдеров
- Автоматическая синхронизация с `localStorage`
- Меньше ре-рендеров

### Внедрение React Hook Form + Zod

До рефакторинга формы управлялись вручную через `useState` + валидация на blur. После рефакторинга:

- `useForm` с `zodResolver` обеспечивает декларативную валидацию
- `mode: 'onChange'` даёт мгновенную обратную связь
- Схемы вынесены в отдельный файл `schemas/booking.schema.ts`

### Извлечение Axios-интерцепторов

До рефакторинга каждый API-вызов вручную добавлял заголовок `Authorization`. После рефакторинга логика централизована в `apiClient.ts`:

```typescript
apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('aviacassa_jwt');
    if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});
```
