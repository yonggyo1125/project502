package org.choongang.chatting.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.choongang.chatting.service.ChatRoomInfoService;
import org.choongang.chatting.service.ChatRoomSaveService;
import org.choongang.commons.ExceptionProcessor;
import org.choongang.commons.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chatting")
@RequiredArgsConstructor
public class ChatController implements ExceptionProcessor {

    private final ChatRoomInfoService chatRoomInfoService;
    private final ChatRoomSaveService chatRoomSaveService;

    private final Utils utils;


    @GetMapping
    public String roomList(Model model) {
        commonProcess("main", model);

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

    private void commonProcess(String mode, Model model) {
        List<String> addCommonScript = new ArrayList<>();
        addCommonScript.add("chat");

        model.addAttribute("addCommonScript", addCommonScript);
    }
}
