package org.choongang.reservation.service;

import lombok.RequiredArgsConstructor;
import org.choongang.member.MemberUtil;
import org.choongang.reservation.controllers.RequestReservation;
import org.choongang.reservation.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

/**
 * 예약 신청 처리
 *
 */
@Service
@RequiredArgsConstructor
public class ReservationApplyService {

    private final ReservationRepository reservationRepository;
    private final MemberUtil memberUtil;

    public void apply(RequestReservation form) {

    }
}
