package org.choongang.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.choongang.chatting.controllers.RequestChatRoom;
import org.choongang.chatting.entities.ChatRoom;
import org.choongang.chatting.service.ChatRoomInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.profiles.active=test")
public class RoomTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatRoomInfoService chatRoomInfoService;

    private ObjectMapper om;

    @BeforeEach
    void init() {
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("방생성 테스트")
    void roomCreateTest() throws Exception {
        String roomId = "room502";
        String roomNm = "강의실502호";
        RequestChatRoom form = new RequestChatRoom();
        form.setRoomId(roomId);
        form.setRoomNm(roomNm);
        form.setCapacity(29);

        String params = om.writeValueAsString(form);

        mockMvc.perform(post("/api/chat/room")
                        .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(params))
                .andExpect(status().isCreated());

        ChatRoom room = chatRoomInfoService.get(roomId);
        assertEquals(roomId, room.getRoomId());
        assertEquals(roomNm, room.getRoomNm());
    }

    @Test
    @DisplayName("필수 항목 검증")
    void requiredFieldsTest() throws Exception {
        RequestChatRoom form = new RequestChatRoom();
        String params = om.writeValueAsString(form);

        mockMvc.perform(post("/api/chat/room")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                        .content(params))
                .andDo(print());
    }

}
