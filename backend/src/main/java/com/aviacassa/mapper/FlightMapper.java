package com.aviacassa.mapper;

import com.aviacassa.dto.FlightDTO;
import com.aviacassa.entity.Flight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FlightMapper {

    @Mapping(source = "basePrice", target = "price")
    FlightDTO toDto(Flight flight);

    List<FlightDTO> toDtoList(List<Flight> flights);
}
