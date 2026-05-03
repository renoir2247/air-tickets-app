package com.aviacassa.controller;

import com.aviacassa.dto.FlightDTO;
import com.aviacassa.entity.Flight;
import com.aviacassa.exception.GlobalExceptionHandler;
import com.aviacassa.mapper.FlightMapper;
import com.aviacassa.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FlightController flightController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(flightController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllFlights_shouldReturnList() throws Exception {
        Flight flight = Flight.builder().id(1L).flightNumber("SU100").build();
        when(flightRepository.findAll()).thenReturn(List.of(flight));
        when(flightMapper.toDtoList(anyList())).thenReturn(
                List.of(FlightDTO.builder().id(1L).flightNumber("SU100").build())
        );

        mockMvc.perform(get("/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].flightNumber").value("SU100"));
    }

    @Test
    void searchFlights_shouldFilterByOrigin() throws Exception {
        Flight flight = Flight.builder()
                .id(1L)
                .origin("Москва")
                .destination("Сочи")
                .build();
        when(flightRepository.findAll()).thenReturn(List.of(flight));
        when(flightMapper.toDtoList(anyList())).thenReturn(
                List.of(FlightDTO.builder().id(1L).origin("Москва").destination("Сочи").build())
        );

        mockMvc.perform(get("/flights/search").param("origin", "мос"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].origin").value("Москва"));
    }
}
