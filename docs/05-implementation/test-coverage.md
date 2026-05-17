# Модульное тестирование и покрытие кода

## Инструменты

- **JUnit 5** — фреймворк модульного тестирования
- **JaCoCo 0.8.11** — измерение покрытия кода тестами
- **Mockito** — создание mock-объектов для изоляции слоёв
- **Spring Boot Test** — интеграционное тестирование с `@WebMvcTest` и `@DataJpaTest`

---

## Результаты тестирования

**Команда:** `mvn test`

**Классы тестов (16 файлов):**

- `AuthControllerTest`
- `BookingControllerTest`
- `FlightControllerTest`
- `PaymentControllerTest`
- `ReportControllerTest`
- `BookingServiceImplTest`
- `PaymentServiceImplTest`
- `CustomUserDetailsServiceTest`
- `JwtServiceTest`
- `JwtAuthenticationFilterTest`
- `CorsConfigTest`
- `SecurityConfigTest`
- `GlobalExceptionHandlerTest`
- `DtoAndEntityTest`
- `PasswordTest`

---

## Сводка покрытия (JaCoCo)

**Команда генерации отчёта:** `mvn test` (JaCoCo плагин привязан к фазе `test`)

### По слоям приложения

- **controller** — тесты с `@WebMvcTest` + `MockMvc`
- **service.impl** — тесты с `@ExtendWith(MockitoExtension.class)`
- **config** — тесты JWT, Security, CORS
- **exception** — тесты глобального обработчика исключений

### Примеры тестов

#### BookingServiceImplTest — бизнес-логика бронирования

```java
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_WithValidData_ShouldReturnBooking() {
        Flight flight = Flight.builder()
            .id(1L)
            .availableSeats(10)
            .basePrice(new BigDecimal("5000.00"))
            .build();

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        List<PassengerDto> passengers = List.of(
            new PassengerDto("Иван", "Иванов", "1234567890", LocalDate.of(1990, 1, 1))
        );

        Booking result = bookingService.createBooking(1L, passengers);

        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());
        assertEquals(9, flight.getAvailableSeats()); // 10 - 1
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_WithInsufficientSeats_ShouldThrowException() {
        Flight flight = Flight.builder()
            .id(1L)
            .availableSeats(1)
            .build();

        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        List<PassengerDto> passengers = List.of(
            new PassengerDto("Иван", "Иванов", "1234567890", LocalDate.of(1990, 1, 1)),
            new PassengerDto("Пётр", "Петров", "0987654321", LocalDate.of(1992, 2, 2))
        );

        assertThrows(InsufficientSeatsException.class,
            () -> bookingService.createBooking(1L, passengers));
    }

    @Test
    void cancelBooking_ShouldRestoreSeats() {
        Flight flight = Flight.builder().id(1L).availableSeats(5).build();
        Booking booking = Booking.builder()
            .id(1L)
            .flight(flight)
            .status(BookingStatus.PENDING)
            .passengers(List.of(new Passenger(), new Passenger()))
            .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        bookingService.cancelBooking(1L);

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(7, flight.getAvailableSeats()); // 5 + 2
    }
}
```

#### JwtServiceTest — JWT-утилиты

```java
@SpringBootTest(classes = JwtService.class)
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generateToken_ShouldReturnValidToken() {
        UserDetails user = User.builder()
            .username("cashier1")
            .password("password")
            .roles("USER")
            .build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        UserDetails user = User.builder()
            .username("cashier1")
            .password("password")
            .roles("USER")
            .build();

        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        UserDetails user = User.builder()
            .username("admin")
            .password("password")
            .roles("ADMIN")
            .build();

        String token = jwtService.generateToken(user);

        assertEquals("admin", jwtService.extractUsername(token));
    }
}
```

---

## Настройка JaCoCo в проекте

### `pom.xml`

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## Запуск тестов

```bash
# Запустить все тесты
mvn test

# Сгенерировать отчёт JaCoCo
mvn test jacoco:report

# Отчёт будет в:
# backend/target/site/jacoco/index.html   (HTML)
# backend/target/jacoco.exec              (binary)
```
