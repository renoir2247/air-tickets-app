package com.aviacassa.controller;

import com.aviacassa.dto.*;
import com.aviacassa.entity.Booking;
import com.aviacassa.entity.enums.BookingStatus;
import com.aviacassa.exception.GlobalExceptionHandler;
import com.aviacassa.mapper.BookingMapper;
import com.aviacassa.service.IBookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IBookingService bookingService;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createBooking_shouldReturnCreated() throws Exception {
        PassengerRequest passengerRequest = PassengerRequest.builder()
                .firstName("Иван")
                .lastName("Иванов")
                .passportNumber("123456")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        BookingRequest request = BookingRequest.builder()
                .flightId(1L)
                .passengers(List.of(passengerRequest))
                .build();

        PassengerDto passengerDto = new PassengerDto("Иван", "Иванов", "123456", LocalDate.of(1990, 1, 1));
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(5000))
                .build();

        when(bookingMapper.passengerRequestListToPassengerDtoList(anyList())).thenReturn(List.of(passengerDto));
        when(bookingService.createBooking(any(), any())).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(
                BookingDTO.builder()
                        .id(1L)
                        .status(BookingStatus.PENDING)
                        .totalAmount(BigDecimal.valueOf(5000))
                        .build()
        );

        String json = "{\"flightId\":1,\"passengers\":[{\"firstName\":\"Иван\",\"lastName\":\"Иванов\",\"passportNumber\":\"123456\",\"dateOfBirth\":\"1990-01-01\"}]}";
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void cancelBooking_shouldReturnNoContent() throws Exception {
        mockMvc.perform(post("/bookings/1/cancel"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllBookings_shouldReturnList() throws Exception {
        Booking booking = Booking.builder().id(1L).status(BookingStatus.PENDING).build();
        when(bookingService.findAll()).thenReturn(List.of(booking));
        when(bookingMapper.toDtoList(anyList())).thenReturn(
                List.of(BookingDTO.builder().id(1L).status(BookingStatus.PENDING).build())
        );

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.CONFIRMED)
                .build();
        when(bookingService.findById(1L)).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(
                BookingDTO.builder().id(1L).status(BookingStatus.CONFIRMED).build()
        );

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void getBookingStatus_shouldReturnStatus() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        when(bookingService.findById(1L)).thenReturn(booking);

        mockMvc.perform(get("/bookings/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
