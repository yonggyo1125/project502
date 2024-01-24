package org.choongang.reservation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test1() throws Exception {
        String url = "/api/reservation/available_capacity?cCode=1&date=2024-01-26&time=09:20";
        mockMvc.perform(get(url))
                .andDo(print());
    }
}
