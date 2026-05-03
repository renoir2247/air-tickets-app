package com.aviacassa.controller;

import com.aviacassa.entity.Booking;
import com.aviacassa.entity.enums.BookingStatus;
import com.aviacassa.exception.GlobalExceptionHandler;
import com.aviacassa.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getSalesReport_shouldReturnReport() throws Exception {
        Booking confirmed = Booking.builder()
                .id(1L)
                .status(BookingStatus.CONFIRMED)
                .totalAmount(BigDecimal.valueOf(10000))
                .createdAt(LocalDateTime.now())
                .build();

        Booking pending = Booking.builder()
                .id(2L)
                .status(BookingStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(5000))
                .createdAt(LocalDateTime.now())
                .build();

        when(bookingRepository.findAll()).thenReturn(List.of(confirmed, pending));

        mockMvc.perform(get("/reports/sales")
                        .param("from", LocalDate.now().minusDays(7).toString())
                        .param("to", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBookings").value(2))
                .andExpect(jsonPath("$.confirmedBookings").value(1))
                .andExpect(jsonPath("$.pendingBookings").value(1))
                .andExpect(jsonPath("$.cancelledBookings").value(0))
                .andExpect(jsonPath("$.totalRevenue").value(10000));
    }
}
