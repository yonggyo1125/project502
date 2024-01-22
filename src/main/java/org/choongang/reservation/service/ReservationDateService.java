package org.choongang.reservation.service;

import lombok.RequiredArgsConstructor;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

@Service("rDateService")
@RequiredArgsConstructor
public class ReservationDateService {

    private final CenterInfoService infoService;

    public boolean checkAvailable(Long cCode, String bookDate) {

        /**
         * 1. 가능 요일 체크
         * 2. 공휴일 여부 체크 -> true : 등록된 공휴일인지 체크
         */

        CenterInfo data = infoService.get(cCode);
        String bookYoil = data.getBookYoil();
        if (!StringUtils.hasText(bookYoil)) { // 예약 가능 요일이 없는 경우
            return false;
        }

        LocalDate date = LocalDate.parse(bookDate); // 2024-01-22

        String yoil = date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN);

        if (!bookYoil.contains(yoil)) {
            return false;
        }

        // 공휴일 예약 불가 -> 공휴일 체크
        if (!data.isBookHday()) { // 공휴일 예약불가

        }


        return true;
    }
}
