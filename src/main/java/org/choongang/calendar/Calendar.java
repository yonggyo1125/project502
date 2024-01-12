package org.choongang.calendar;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Lazy
@Component
public class Calendar {


    /**
     * 달력 데이터
     * 7 * 6 (42), 7 * 5(35)
     */
    public Map<String, Object> getData(Integer _year, Integer _month) {
        int year, month = 0;
        if (_year == null || _month == null) { // 년도와 월 값이 없으면 현재 년도, 월로 고정
            LocalDate today = LocalDate.now();
            year = today.getYear();
            month = today.getMonthValue();
        } else {
            year = _year.intValue();
            month = _month.intValue();
        }

        LocalDate sdate = LocalDate.of(year, month, 1);
        LocalDate eDate = sdate.plusMonths(1L).minusDays(1);
        int sYoil = sdate.getDayOfWeek().getValue(); // 매월 1일 요일

        sYoil = sYoil == 7 ? 0 : sYoil;

        int start = sYoil * -1;

        int cellNum = sYoil + eDate.getDayOfMonth() > 35 ? 42 : 35;


        Map<String, Object> data = new HashMap<>();

        List<String> days = new ArrayList<>(); // 날짜, 1, 2, 3,
        List<String> dates = new ArrayList<>(); // 날짜 문자열 2024-01-12
        List<String> yoils = new ArrayList<>(); // 요일 정보

        for (int i = start; i < cellNum + start; i++) {
            LocalDate date = sdate.plusDays(i);

            int yoil = date.getDayOfWeek().getValue();
            yoil = yoil == 7 ? 0 : yoil; // 0 ~ 6 (일 ~ 토)
            days.add(String.valueOf(date.getDayOfMonth()));
            dates.add(date.toString());
            yoils.add(String.valueOf(yoil));

            data.put("days", days);
            data.put("dates", dates);
            data.put("yoils", yoils);
        }

        // 이전달 년도, 월
        LocalDate prevMonthDate = sdate.minusMonths(1L);
        data.put("prevYear", String.valueOf(prevMonthDate.getYear())); // 이전달 년도
        data.put("prevMonth", String.valueOf(prevMonthDate.getMonthValue())); // 이전달 월
                
        // 다음달 년도, 월
        LocalDate nextMonthDate = sdate.plusMonths(1L);
        data.put("nextYear", String.valueOf(nextMonthDate.getYear())); // 다음달 년도
        data.put("nextMonth", String.valueOf(nextMonthDate.getMonthValue())); // 다음달 월

        // 현재 년도, 월
        data.put("year", String.valueOf(year));
        data.put("month", String.valueOf(month));

        // 요일 제목
        data.put("yoilTitles", getYoils());

        return data;
    }

    public Map<String, Object> getData() {
        return getData(0, 0);
    }

    public List<String> getYoils() {

        return Arrays.asList(
            "일", "월", "화", "수", "목", "금", "토"
        );
    }
}
