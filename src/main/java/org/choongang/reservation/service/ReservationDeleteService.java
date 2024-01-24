package org.choongang.reservation.service;

import lombok.RequiredArgsConstructor;
import org.choongang.commons.Utils;
import org.choongang.commons.exceptions.AlertException;
import org.choongang.reservation.entities.Reservation;
import org.choongang.reservation.repositories.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationDeleteService {
    private final ReservationRepository repository;
    private final Utils utils;

    public void deleteList(List<Integer> chks) {
        if (chks == null || chks.isEmpty()) {
            throw new AlertException("삭제할 예약을 선택하세요.", HttpStatus.BAD_REQUEST);
        }

        for (int chk : chks) {
            Long bookCode = Long.valueOf(utils.getParam("bookCode_" + chk));
            Reservation reservation = repository.findById(bookCode).orElse(null);
            if (reservation == null) {
                continue;
            }

            repository.delete(reservation);
        }

        repository.flush();
    }
}
