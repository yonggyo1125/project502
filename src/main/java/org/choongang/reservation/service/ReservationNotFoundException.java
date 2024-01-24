package org.choongang.reservation.service;

import org.choongang.commons.Utils;
import org.choongang.commons.exceptions.AlertBackException;
import org.springframework.http.HttpStatus;

public class ReservationNotFoundException extends AlertBackException {
    public ReservationNotFoundException() {
        super(Utils.getMessage("NotFound.reservation", "errors"), HttpStatus.NOT_FOUND);
    }
}
