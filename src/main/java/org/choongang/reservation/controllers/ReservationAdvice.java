package org.choongang.reservation.controllers;

import org.choongang.reservation.constants.DonationType;
import org.choongang.reservation.constants.ReservationStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice("org.choongang")
public class ReservationAdvice {
    @ModelAttribute("donationTypes")
    public List<String[]> getDonationTypes() {
        return DonationType.getList();
    }

    @ModelAttribute("reservationStatuses")
    public List<String[]> getStatuses() {
        return ReservationStatus.getList();
    }
}
