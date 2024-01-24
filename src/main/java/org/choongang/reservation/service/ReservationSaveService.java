package org.choongang.reservation.service;

import lombok.RequiredArgsConstructor;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.choongang.commons.Utils;
import org.choongang.commons.exceptions.AlertException;
import org.choongang.reservation.constants.DonationType;
import org.choongang.reservation.constants.ReservationStatus;
import org.choongang.reservation.controllers.RequestReservation;
import org.choongang.reservation.entities.Reservation;
import org.choongang.reservation.repositories.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationSaveService {
    private final ReservationInfoService infoService;
    private final ReservationRepository repository;
    private final CenterInfoService centerInfoService;

    private final Utils utils;

    /**
     * 예약 정보 수정
     *
     * @param form
     */
    public void save(RequestReservation form) {
        Long bookCode = form.getBookCode();
        Reservation data = infoService.get(bookCode);
        CenterInfo center = centerInfoService.get(form.getCCode());

        data.setCenter(center);
        data.setStatus(ReservationStatus.valueOf(form.getStatus()));
        data.setBookType(DonationType.valueOf(form.getBookType()));
        data.setCapacity(form.getPersons());
        data.setDonorTel(form.getDonorTel());
        data.setBookDateTime(LocalDateTime.of(form.getDate(), form.getTime()));

        repository.saveAndFlush(data);
    }

    /**
     * 목록 수정
     * 
     * @param chks : 선택 번호
     */
    public void saveList(List<Integer> chks) {
        if (chks == null || chks.isEmpty()) {
            throw new AlertException("수정할 예약을 선택하세요.", HttpStatus.BAD_REQUEST);
        }

        for(int chk : chks) {
            Long bookCode = Long.valueOf(utils.getParam("bookCode_" + chk));
            Reservation reservation = repository.findById(bookCode).orElse(null);
            if (reservation == null) {
                continue;
            }

            ReservationStatus status = ReservationStatus.valueOf(utils.getParam("status_" + chk));
            reservation.setStatus(status);
        }

        repository.flush();
    }
}
