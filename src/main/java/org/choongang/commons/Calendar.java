package org.choongang.commons;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Calendar {

    /**
     * 매월의 1일 ~ 끝나는 일자
     *
     * 끝나는 일자? -> 다음달 1일에서 -1일
     * @return
     */
    public int[] getDays(int year, int month) {
        LocalDate sdate = LocalDate.of(year, month, 1);
        LocalDate edate = sdate.plusMonths(1L).minusDays(1);

        int[] days = IntStream.rangeClosed(sdate.getDayOfMonth(), edate.getDayOfMonth()).toArray();

        return days;
    }

    /**
     * 달력 데이터
     * 7 * 6 (42), 7 * 5(35)
     * @param year
     * @param month
     */
    public Map<String, List<String>> getData(int year, int month) {
        LocalDate sdate = LocalDate.of(year, month, 1);
        LocalDate eDate = sdate.plusMonths(1L).minusDays(1);
        int sYoil = sdate.getDayOfWeek().getValue(); // 매월 1일 요일

        sYoil = sYoil == 7 ? 0 : sYoil;

        int start = sYoil * -1;

        int cellNum = sYoil + eDate.getDayOfMonth() > 35 ? 42 : 35;


        Map<String, List<String>> data = new HashMap<>();

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

        return data;
    }
}
