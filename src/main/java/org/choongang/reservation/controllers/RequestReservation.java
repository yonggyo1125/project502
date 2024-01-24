package org.choongang.reservation.controllers;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.choongang.member.entities.Member;
import org.choongang.reservation.constants.DonationType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class RequestReservation {

    private Long bookCode;

    @NonNull
    private Long cCode; // 선터 코드
    private String mode = "step1";

    @NotBlank
    private String status; // 예약 상태

    @NonNull
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate date; // 예약 일자

    @NonNull
    @DateTimeFormat(pattern="HH:mm")
    private LocalTime time; // 예약 시간

    @Min(1)
    private int persons; // 예약 인원수

    @NotBlank
    private String donorTel;

    @NotBlank
    private String bookType = DonationType.TYPE_ALL.name(); // 헌혈 타입

    private Integer year; // 달력 년
    private Integer month;  // 달력 월

    private Member member;
}
