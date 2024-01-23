package org.choongang.reservation.controllers;

import lombok.Data;
import org.choongang.reservation.constants.DonationType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RequestReservation {

    private Long cCode; // 선터 코드
    private String mode = "step1";
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate date; // 예약 일자

    @DateTimeFormat(pattern="HH:mm")
    private LocalTime time; // 예약 시간

    private int persons; // 예약 인원수

    private String donorTel;

    private String bookType = DonationType.TYPE_ALL.name(); // 헌혈 타입

    private Integer year; // 달력 년
    private Integer month;  // 달력 월
}
