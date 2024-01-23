package org.choongang.reservation.controllers;

import lombok.Data;

@Data
public class ReservationSearch {
    private int page = 1;
    private int limit = 20;


    private String userId;
}
