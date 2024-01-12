package org.choongang.calendar;

import org.choongang.commons.Calendar;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
public class CalendarTest {

    @Test
    void test1() {
        Calendar cal = new Calendar();
        int[] days = cal.getDays(2024, 2);
        System.out.println(Arrays.toString(days));
    }

    @Test
    void test2() {
        Calendar cal = new Calendar();
        cal.getData(2024, 2);
    }
}
