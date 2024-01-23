package org.choongang.reservation.service;

import lombok.RequiredArgsConstructor;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.choongang.commons.ListData;
import org.choongang.reservation.controllers.ReservationSearch;
import org.choongang.reservation.entities.Reservation;
import org.choongang.reservation.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReservationInfoService {
    private final ReservationRepository reservationRepository;
    private final CenterInfoService centerInfoService;


    public Reservation get(Long bookCode) {

        return null;
    }

    public ListData<Reservation> getList(ReservationSearch search) {

        return null;
    }

    public int getAvailableCapacity(Long cCode, LocalDateTime bookDateTime) {

        CenterInfo center = centerInfoService.get(cCode);

        Integer current = reservationRepository.getTotalCapacity(cCode, bookDateTime);

        int curr = Objects.requireNonNullElse(current, 0);

        return center.getBookCapacity() - curr;
    }
}
