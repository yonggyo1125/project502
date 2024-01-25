package org.choongang.reservation;

import org.choongang.reservation.repositories.ReservationRepository;
import org.choongang.reservation.service.ReservationInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class ReservationDateServiceTest2 {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationInfoService reservationInfoService;

    @Test
    void test1() {
        Long cCode = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 29, 9, 20);
        Integer capacity = reservationRepository.getTotalCapacity(cCode, dateTime);

        System.out.println(capacity);
    }

    @Test
    void test2() {
        Long cCode = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 29, 9, 20);

        int available = reservationInfoService.getAvailableCapacity(cCode, dateTime);
        System.out.println(available);
        // assertEquals(0, available);
    }
}
