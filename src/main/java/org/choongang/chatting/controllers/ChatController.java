package org.choongang.chatting.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.choongang.chatting.entities.ChatHistory;
import org.choongang.chatting.entities.ChatRoom;
import org.choongang.chatting.service.ChatHistoryInfoService;
import org.choongang.chatting.service.ChatRoomInfoService;
import org.choongang.chatting.service.ChatRoomSaveService;
import org.choongang.commons.ExceptionProcessor;
import org.choongang.commons.ListData;
import org.choongang.commons.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chatting")
@RequiredArgsConstructor
public class ChatController implements ExceptionProcessor {

    private final ChatRoomInfoService chatRoomInfoService;
    private final ChatRoomSaveService chatRoomSaveService;
    private final ChatHistoryInfoService chatHistoryInfoService;

    private final Utils utils;

    private ChatRoom chatRoom;

    @GetMapping
    public String roomList(@ModelAttribute ChatRoomSearch search, Model model) {
        commonProcess("room_list", model);

        ListData<ChatRoom> data = chatRoomInfoService.getList(search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return utils.tpl("chat/rooms");
    }

    @GetMapping("/create")
    public String createRoom(@ModelAttribute RequestChatRoom form, Model model) {
        commonProcess("create_room", model);

        return utils.tpl("chat/create_room");
    }

    @PostMapping("/create")
    public String createRoomPs(@Valid RequestChatRoom form, Errors errors, Model model) {
        commonProcess("create_room", model);

        if (errors.hasErrors()) {
            return utils.tpl("chat/create_room");
        }

        chatRoomSaveService.save(form);

        return "redirect:/chatting/" + form.getRoomId();
    }

    // 채팅 방
    @GetMapping("/{roomId}")
    public String chattingRoom(@PathVariable("roomId") String roomId, Model model) {
        commonProcess(roomId, "chat_room", model);

        List<ChatHistory> items = chatHistoryInfoService.getList(roomId);

        model.addAttribute("items", items);

        return utils.tpl("chat/room");
    }

    private void commonProcess(String roomId, String mode, Model model) {
        chatRoom = chatRoomInfoService.get(roomId);

        commonProcess(mode, model);

        model.addAttribute("chatRoom", chatRoom);
    }

    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "room_list";
        String pageTitle = Utils.getMessage("채팅방_목록", "commons");

        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();

        addCss.add("chat/style");

        if (mode.equals("create_room")) {
            pageTitle = Utils.getMessage("채팅방_생성", "commons");
        } else if (mode.equals("chat_room")) {
            pageTitle = chatRoom.getRoomNm();
            addScript.add("chat/room");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("pageTitle", pageTitle);
    }
}