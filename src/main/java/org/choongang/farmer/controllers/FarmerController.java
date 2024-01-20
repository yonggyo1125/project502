package org.choongang.farmer.controllers;

import org.choongang.board.controllers.AbstractBoardController;
import org.choongang.board.controllers.BoardDataSearch;
import org.choongang.board.controllers.BoardFormValidator;
import org.choongang.board.entities.BoardData;
import org.choongang.board.service.BoardAuthService;
import org.choongang.board.service.BoardDeleteService;
import org.choongang.board.service.BoardInfoService;
import org.choongang.board.service.BoardSaveService;
import org.choongang.board.service.config.BoardConfigInfoService;
import org.choongang.commons.ListData;
import org.choongang.commons.Utils;
import org.choongang.file.service.FileInfoService;
import org.choongang.member.MemberUtil;
import org.choongang.member.entities.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/farmer")
public class FarmerController extends AbstractBoardController {

    public FarmerController(BoardConfigInfoService configInfoService, FileInfoService fileInfoService, BoardFormValidator boardFormValidator, BoardSaveService boardSaveService, BoardInfoService boardInfoService, BoardDeleteService boardDeleteService, BoardAuthService boardAuthService, MemberUtil memberUtil, Utils utils) {
        super(configInfoService, fileInfoService, boardFormValidator, boardSaveService, boardInfoService, boardDeleteService, boardAuthService, memberUtil, utils);
    }


    @GetMapping
    public String index(@RequestParam(name="tab", defaultValue = "intro") String tab,
                        @ModelAttribute BoardDataSearch search, Model model) {

        model.addAttribute("tab", tab);

        if (tab.equals("intro")) {

        } else if (tab.equals("blog")) {
            // 회원 가입 -> 농장 -> 게시판 하나 생성(blog_아이디)
            Member member = memberUtil.getMember();
            String bid = "blog_" + member.getUserId();
            commonProcess(bid, "list", model);

            ListData<BoardData> data = boardInfoService.getList(bid, search);
            model.addAttribute("board", board);
            model.addAttribute("items", data.getItems());
            model.addAttribute("pagination", data.getPagination());

        }

        List<String> addCss = (List<String>)model.getAttribute("addCss");
        addCss = Objects.requireNonNullElse(addCss, new ArrayList<>());
        addCss.add("farmer/style");

        model.addAttribute("addCss", addCss);

        return utils.tpl("farmer/index");
    }
}

