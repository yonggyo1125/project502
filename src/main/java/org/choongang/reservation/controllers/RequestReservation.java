package org.choongang.reservation.controllers;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class RequestReservation {

    private Long cCode; // 선터 코드
    private String mode = "step1";
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate date; // 예약 일자
    private String time; // 예약 시간
    private int persons; // 예약 인원수

    private String personsInfo; // 방문자 정보 - JSON으로 가공 처리

    private Integer year; // 달력 년
    private Integer month;  // 달력 월
}
