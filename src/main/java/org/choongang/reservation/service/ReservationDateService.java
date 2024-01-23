package org.choongang.reservation.service;

import lombok.RequiredArgsConstructor;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service("rDateService")
@RequiredArgsConstructor
public class ReservationDateService {

    private final CenterInfoService infoService;
    private final ReservationInfoService reservationInfoService;

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

        if (!date.isAfter(LocalDate.now())) {
            return false;
        }

        String yoil = date.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN);

        if (!bookYoil.contains(yoil)) {
            return false;
        }

        // 공휴일 예약 불가 -> 공휴일 체크
        if (!data.isBookHday()) { // 공휴일 예약불가

        }


        return true;
    }

    /**
     * 예약블록
     * 09:00
     * 09:20
     * 09:40
     */
    public List<LocalTime> getAvailableTimes(Long cCode, LocalDate date) {

        List<LocalTime> times = new ArrayList<>();

        CenterInfo centerInfo = infoService.get(cCode);

        int bookBlock = centerInfo.getBookBlock();
        String bookAvl = centerInfo.getBookAvl();
        String bookNotAvl = centerInfo.getBookNotAvl();

        int capacity = centerInfo.getBookCapacity(); // 신청 가능 인원수, -1 : 무제한, 0 - 신청 불가

        if (StringUtils.hasText(bookAvl) && capacity != 0) {

            /* 예약 불가 블록 가공 */
            List<LocalTime[]> bookNotAvls = StringUtils.hasText(bookNotAvl) ?
                    Arrays.stream(bookNotAvl.trim().split("\\n"))
                            .map(s -> s.trim().replaceAll("\\r", ""))
                            .map(s -> s.split("-"))
                            .map(s -> new LocalTime[] { LocalTime.parse(s[0]), LocalTime.parse(s[1])})
                            .toList() : null;

            String[] bookAvls = bookAvl.split("-");
            LocalTime sTime = LocalTime.parse(bookAvls[0]);
            LocalTime eTime = LocalTime.parse(bookAvls[1]);

            times.add(sTime);

            LocalTime tmpTime = sTime;
            while(true) {
                LocalTime newTime = tmpTime.plusMinutes(bookBlock);
                tmpTime = newTime;
                if (newTime.equals(eTime) || newTime.isAfter(eTime)) {
                    break;
                }

                /* 예약 불가 블록에 있는 경우 배제 */
                if (bookNotAvls != null) {
                    boolean notAvailable = bookNotAvls.stream()
                                .anyMatch(t -> (newTime.equals(t[0])
                                                || newTime.isAfter(t[0]))
                                                && newTime.isBefore(t[1]));

                    if (notAvailable) {
                        continue;
                    }
                } // endif

                /**
                 * 신청 인원수 : 예약 테이블에서 체크 -> 예약가능한 인원수가 X -> continue;
                 */


                times.add(newTime);
            }

        } // endif

        /* 예약 가능 인원수 체크 S */
        times = times.stream().filter(t -> reservationInfoService.getAvailableCapacity(cCode, LocalDateTime.of(date, t)) > 0).toList();
        /* 예약 가능 인원수 체크 E */

        return times;
    }

}
