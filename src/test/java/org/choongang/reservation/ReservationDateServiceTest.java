package org.choongang.reservation;

import org.choongang.admin.center.RequestCenter;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.choongang.center.service.CenterSaveService;
import org.choongang.reservation.service.ReservationDateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = "spring.profiles.active=test")
public class ReservationDateServiceTest {

    @Autowired
    private CenterSaveService saveService;

    @Autowired
    private CenterInfoService infoService;

    @Autowired
    private ReservationDateService dateService;

    private RequestCenter form;

    @BeforeEach
    void init() {
        form = new RequestCenter();
        form.setCName("센터1");
        form.setZonecode("01234");
        form.setAddress("주소");
        form.setTelNum("010");
        form.setOperHour("운영시간");
        form.setBookYoil(Arrays.asList("월", "수", "금"));
        form.setBookAvlShour("09");
        form.setBookAvlSmin("00");
        form.setBookAvlEhour("18");
        form.setBookAvlEmin("00");
        form.setBookNotAvl("13:00-14:00\n16:00-17:00");
        form.setBookHday(false);
        form.setBookBlock(20);
        form.setBookCapacity(3);

        CenterInfo centerInfo = saveService.save(form);
        form.setCCode(centerInfo.getCCode());

        /*
        private String bookAvlShour; // 예약 가능 시작 시간
        private String bookAvlSmin; // 예약 가능 시작 분
        private String bookAvlEhour; // 예약 가능 종료 시간
        private String bookAvlEmin; // 예약 가능 종료 분
        private String bookNotAvl; // 예약 불가 시간
        private boolean bookHday; // 공휴일 예약 가능 여부
        private int bookBlock; // 예약 블록
        private int bookCapacity; // -1이면 무제한, 0이면 예약 불가

         */
    }

    @Test
    void test1() {
        // 가능 요일 테스트
        boolean result1 = dateService.checkAvailable(form.getCCode(), "2024-01-24");
        assertTrue(result1);

        
        // 불가능 요일 테스트
        boolean result2 = dateService.checkAvailable(form.getCCode(), "2024-01-25");
        assertTrue(!result2);
    }
}
