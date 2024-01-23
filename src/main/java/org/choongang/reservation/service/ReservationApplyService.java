package org.choongang.reservation.service;

import lombok.RequiredArgsConstructor;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.choongang.member.MemberUtil;
import org.choongang.reservation.constants.DonationType;
import org.choongang.reservation.constants.ReservationStatus;
import org.choongang.reservation.controllers.RequestReservation;
import org.choongang.reservation.entities.Reservation;
import org.choongang.reservation.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 예약 신청 처리
 *
 */
@Service
@RequiredArgsConstructor
public class ReservationApplyService {

    private final ReservationRepository reservationRepository;
    private final CenterInfoService centerInfoService;
    private final MemberUtil memberUtil;

    public Reservation apply(RequestReservation form) {

        CenterInfo center = centerInfoService.get(form.getCCode());

        LocalDateTime bookDateTime = LocalDateTime.of(form.getDate(), form.getTime());

        Reservation reservation = Reservation.builder()
                .bookDateTime(bookDateTime)
                .bookType(DonationType.valueOf(form.getBookType()))
                .center(center)
                .member(memberUtil.getMember())
                .capacity(form.getPersons())
                .status(ReservationStatus.APPLY)
                .donorTel(form.getDonorTel())
                .build();

        reservationRepository.saveAndFlush(reservation);

        return reservation;
    }
}
