package org.choongang.board.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.board.entities.BoardData;
import org.choongang.board.repositories.BoardDataRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardDataRepository boardDataRepository;

    @ResponseBody
    @GetMapping("/test")
    public void test() {
        BoardData data = new BoardData();
        data.setSubject("제목");
        data.setContent("내용");
        boardDataRepository.saveAndFlush(data);
    }
}
