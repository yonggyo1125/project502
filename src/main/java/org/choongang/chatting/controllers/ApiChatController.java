package org.choongang.chatting.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.chatting.entities.ChatRoom;
import org.choongang.chatting.service.ChatHistoryInfoService;
import org.choongang.chatting.service.ChatHistorySaveService;
import org.choongang.chatting.service.ChatRoomInfoService;
import org.choongang.chatting.service.ChatRoomSaveService;
import org.choongang.commons.ExceptionRestProcessor;
import org.choongang.commons.ListData;
import org.choongang.commons.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ApiChatController implements ExceptionRestProcessor {

    private final ChatRoomInfoService chatRoomInfoService;
    private final ChatRoomSaveService chatRoomSaveService;
    private final ChatHistorySaveService chatHistorySaveService;
    private final ChatHistoryInfoService chatHistoryInfoService;

    /**
     * 방목록 조회
     * @param search
     * @return
     */
    @GetMapping("/room")
    public JSONData<ListData<ChatRoom>> getRooms(@RequestBody ChatRoomSearch search) {
        ListData<ChatRoom> data = chatRoomInfoService.getList(search);

        return new JSONData<>(data);
    }

    /**
     * 채팅방 추가 및 수정
     *
     * @param form
     * @return
     */
    @PostMapping("/room")
    public ResponseEntity saveRoom(@RequestBody RequestChatRoom form) {
        chatRoomSaveService.save(form);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/room/{roomId}")
    public JSONData<ChatRoom> getRoom(@PathVariable("roomId") String roomId) {
        ChatRoom data = chatRoomInfoService.get(roomId);

        return new JSONData<>(data);
    }

    /**
     * 채팅 메세지 기록 저장
     *
     * @return
     */
    @PostMapping
    public ResponseEntity messageSave(@RequestBody RequestChatHistory form) {
        chatHistorySaveService.save(form);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
