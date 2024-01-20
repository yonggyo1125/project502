package org.choongang.farmer.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.board.controllers.BoardDataSearch;
import org.choongang.board.entities.Board;
import org.choongang.board.entities.BoardData;
import org.choongang.board.service.BoardInfoService;
import org.choongang.board.service.config.BoardConfigInfoService;
import org.choongang.commons.ListData;
import org.choongang.commons.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/farmer")
@RequiredArgsConstructor
public class FarmerController {

    private final Utils utils;
    private final BoardConfigInfoService boardConfigInfoService;
    private final BoardInfoService boardInfoService;

    @ModelAttribute("addCss")
    private String[] addCss() {

        return new String[] { "farmer/style" };
    }

    @GetMapping
    public String index(@RequestParam(name="tab", defaultValue = "intro") String tab,
                        @ModelAttribute BoardDataSearch search, Model model) {

        model.addAttribute("tab", tab);

        if (tab.equals("intro")) {

        } else if (tab.equals("blog")) {
            // 회원 가입 -> 농장 -> 게시판 하나 생성(blog_아이디)
            String bid = "freetalk";
            Board board = boardConfigInfoService.get(bid);

            ListData<BoardData> data = boardInfoService.getList(bid, search);
            model.addAttribute("board", board);
            model.addAttribute("items", data.getItems());
            model.addAttribute("pagination", data.getPagination());
        }

        return utils.tpl("farmer/index");
    }
}

