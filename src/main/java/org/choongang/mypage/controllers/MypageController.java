package org.choongang.mypage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.choongang.board.controllers.BoardDataSearch;
import org.choongang.board.entities.BoardData;
import org.choongang.board.service.SaveBoardDataService;
import org.choongang.commons.ExceptionProcessor;
import org.choongang.commons.ListData;
import org.choongang.commons.RequestPaging;
import org.choongang.commons.Utils;
import org.choongang.file.entities.FileInfo;
import org.choongang.file.service.FileInfoService;
import org.choongang.member.MemberUtil;
import org.choongang.member.entities.Member;
import org.choongang.member.service.follow.FollowBoardService;
import org.choongang.member.service.follow.FollowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController implements ExceptionProcessor {

    private final FileInfoService fileInfoService;
    private final SaveBoardDataService saveBoardDataService;
    private final FollowBoardService followBoardService;
    private final FollowService followService;

    private final MemberUtil memberUtil;

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

    @GetMapping("/follow")
    public String followList(@RequestParam(name="mode", defaultValue = "follower") String mode, RequestPaging paging, Model model) {
        commonProcess("follow", model);

        ListData<Member> data = followService.getList(mode, paging);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());
        model.addAttribute("mode", mode);

        return utils.tpl("mypage/follow");
    }


    @GetMapping("/follow/{userId}")
    public String followBoard(@PathVariable("userId") String userId,
                              @RequestParam(name="mode", defaultValue="follower") String mode,
                              @ModelAttribute BoardDataSearch search, Model model) {

        // 전체 조회가 아니라면 아이디별 조회
        if (!userId.equals("all")) {
            search.setUserId(userId);
        } else {
            search.setUserId(null);
        }

        ListData<BoardData> data = followBoardService.getList(mode, search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return utils.tpl("mypage/follow_board");
    }

    @GetMapping("/profile")
    public String profile(@ModelAttribute RequestProfile form, Model model) {
        commonProcess("profile", model);

        Member member = memberUtil.getMember();
        form.setName(member.getName());
        form.setProfileImage(member.getProfileImage());

        return utils.tpl("mypage/profile");
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid RequestProfile form, Errors errors, Model model) {
        commonProcess("profile", model);

        if (errors.hasErrors()) {

            String gid = memberUtil.getMember().getGid();
            List<FileInfo> profileImages = fileInfoService.getList(gid);

            form.setProfileImage(profileImages.get(0));

            return utils.tpl("mypage/profile");
        }



        return "redirect:/mypage";
    }

    private void commonProcess(String mode, Model model) {
        mode = StringUtils.hasText(mode) ? mode : "main";
        String pageTitle = Utils.getMessage("마이페이지", "commons");

        List<String> addCss = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        List<String> addCommonScript = new ArrayList<>();


        if (mode.equals("save_post")) { // 찜한 게시글 페이지
            pageTitle = Utils.getMessage("찜_게시글", "commons");

            addScript.add("board/common");
            addScript.add("mypage/save_post");
        } else if (mode.equals("follow")) {
            addCommonScript.add("follow");

        } else if (mode.equals("profile")) {
            pageTitle = Utils.getMessage("회원정보_수정", "commons");
            addCommonScript.add("fileManager");
            addScript.add("mypage/profile");
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCommonScript", addCommonScript);
    }
}