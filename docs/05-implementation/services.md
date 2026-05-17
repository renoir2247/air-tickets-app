# Реализация сервисного слоя (Mediator)

Сервисы содержат бизнес-логику и транзакционные границы. Управляют правами доступа и оркестрируют операции с репозиториями.

---

## BookingServiceImpl

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    @Override
    @Transactional
    public Booking createBooking(Long flightId, List<PassengerDto> passengers) {
        Flight flight = flightRepository.findById(flightId)
            .orElseThrow(() -> new EntityNotFoundException("Рейс не найден: " + flightId));

        if (flight.getAvailableSeats() < passengers.size()) {
            throw new InsufficientSeatsException(
                "Недостаточно мест: доступно " + flight.getAvailableSeats()
                + ", запрошено " + passengers.size()
            );
        }

        flight.setAvailableSeats(flight.getAvailableSeats() - passengers.size());

        Booking booking = Booking.builder()
            .bookingReference(generateBookingReference())
            .flight(flight)
            .status(BookingStatus.PENDING)
            .totalAmount(flight.getBasePrice().multiply(BigDecimal.valueOf(passengers.size())))
            .expiresAt(LocalDateTime.now().plusMinutes(30))
            .passengers(passengers.stream().map(this::toPassengerEntity).toList())
            .build();

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено: " + bookingId));

        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getPassengers().size());

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking findById(Long bookingId) {
        return bookingRepository.findById(bookingId)
            .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено: " + bookingId));
    }
}
```

---

## PaymentServiceImpl

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Payment initPayment(Long bookingId, PaymentMethod paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new EntityNotFoundException("Бронирование не найдено: " + bookingId));

        Payment payment = Payment.builder()
            .booking(booking)
            .transactionId(UUID.randomUUID().toString())
            .amount(booking.getTotalAmount())
            .status(PaymentStatus.PENDING)
            .paymentMethod(paymentMethod)
            .build();

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void processWebhook(String transactionId, PaymentStatus status) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new EntityNotFoundException("Платёж не найден: " + transactionId));

        payment.setStatus(status);
        if (status == PaymentStatus.PAID) {
            payment.setPaidAt(LocalDateTime.now());
            Booking booking = payment.getBooking();
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        }
        paymentRepository.save(payment);
    }
}
```

---

## CustomUserDetailsService

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().name().replace("ROLE_", ""))
            .build();
    }
}
```

---

## JwtService

```java
@Service
public class JwtService {

    @Value("${jwt.secret:defaultSecretKeyForDevelopmentEnvironmentOnly}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).toList());
        return buildToken(claims, userDetails, jwtExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
            && !isTokenExpired(token);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```
