package com.aviacassa.repository;

import com.aviacassa.entity.Booking;
import com.aviacassa.entity.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime createdAt);
}
