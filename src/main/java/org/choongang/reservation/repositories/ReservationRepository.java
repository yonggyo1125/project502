package org.choongang.reservation.repositories;

import org.choongang.reservation.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, QuerydslPredicateExecutor<Reservation> {

    @Query("SELECT SUM(r.capacity) FROM Reservation r WHERE r.center.cCode = :cCode AND r.bookDateTime = :dateTime AND r.status <> org.choongang.reservation.constants.ReservationStatus.CANCEL")
    Integer getTotalCapacity(@Param("cCode") Long cCode, @Param("dateTime") LocalDateTime dateTime);
}
