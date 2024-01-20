package org.choongang.mypage.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.board.controllers.BoardDataSearch;
import org.choongang.board.entities.BoardData;
import org.choongang.board.service.SaveBoardDataService;
import org.choongang.commons.ExceptionProcessor;
import org.choongang.commons.ListData;
import org.choongang.commons.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController implements ExceptionProcessor {

    private final SaveBoardDataService saveBoardDataService;
    private final Utils utils;


    @GetMapping // 마이페이지 메인
    public String index(Model model) {
        commonProcess("main", model);

        return utils.tpl("mypage/index");
    }

    /**
     * 찜 게시글 목록
     *
     * @param search
     * @param model
     * @return
     */
    @GetMapping("/save_post")
    public String savePost(@ModelAttribute BoardDataSearch search, Model model) {
        commonProcess("save_post", model);

        ListData<BoardData> data = saveBoardDataService.getList(search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return utils.tpl("mypage/save_post");
    }

    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "main";
        String pageTitle = Utils.getMessage("마이페이지", "commons");

        if (mode.equals("save_post")) { // 찜한 게시글 페이지
            pageTitle = Utils.getMessage("찜_게시글", "commons");
        }

        model.addAttribute("pageTitle", pageTitle);
    }

}