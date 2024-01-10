package org.choongang.admin.member.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.admin.menus.Menu;
import org.choongang.admin.menus.MenuDetail;
import org.choongang.commons.ExceptionProcessor;
import org.choongang.commons.ListData;
import org.choongang.member.controllers.MemberSearch;
import org.choongang.member.entities.Member;
import org.choongang.member.service.MemberInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller("adminMemberController")
@RequestMapping("/admin/member")
@RequiredArgsConstructor
public class MemberController implements ExceptionProcessor {

    private final MemberInfoService infoService;

    @ModelAttribute("menuCode")
    public String getMenuCode() {
        return "member";
    }

    @ModelAttribute("subMenus")
    public List<MenuDetail> getSubMenus() {

        return Menu.getMenus("member");
    }

    @GetMapping
    public String list(@ModelAttribute MemberSearch search, Model model) {

        ListData<Member> data = infoService.getList(search);

        model.addAttribute("items", data.getItems()); // 목록
        model.addAttribute("pagination", data.getPagination()); // 페이징

        model.addAttribute("subMenuCode", "list");
        return "admin/member/list";
    }
}
