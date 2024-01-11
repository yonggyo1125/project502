package org.choongang.admin.product.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.choongang.admin.menus.Menu;
import org.choongang.admin.menus.MenuDetail;
import org.choongang.commons.ExceptionProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller("adminProductController")
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductController implements ExceptionProcessor {

    @ModelAttribute("menuCode")
    public String getMenuCode() {
        return "product";
    }

    @ModelAttribute("subMenus")
    public List<MenuDetail> getSubMenus() {
        return Menu.getMenus("product");
    }

    /**
     * 상품 목록
     *
     * @return
     */
    @GetMapping
    public String list(Model model) {
        commonProcess("list", model);

        return "admin/product/list";
    }

    /**
     * 상품 등록
     *
     * @param model
     * @return
     */
    @GetMapping("/add")
    public String add(Model model) {
        commonProcess("add", model);

        return "admin/product/add";
    }


    /**
     * 상품 등록, 수정 처리
     *
     * @param model
     * @return
     */
    @PostMapping("/save")
    public String save(Model model) {

        return "redirect:/admin/product";
    }

    /**
     * 상품 분류
     *
     * @param model
     * @return
     */
    @GetMapping("/category")
    public String category(@ModelAttribute RequestCategory form, Model model) {
        commonProcess("category", model);

        return "admin/product/category";
    }

    /**
     * 상품 분류 추가, 수정
     *
     * @param model
     * @return
     */
    @PostMapping("/category")
    public String categoryPs(@Valid RequestCategory form, Errors errors, Model model) {
        commonProcess("category", model);

        return "admin/product/category";
    }

    /**
     * 공통 처리 부분
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = Objects.requireNonNullElse(mode, "list");
        String pageTitle = "상품 목록";

        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        if (mode.equals("add") || mode.equals("edit")) {
            pageTitle = mode.equals("edit") ? "상품 수정" : "상품 등록";
            addCommonScript.add("ckeditor5/ckeditor");
            addCommonScript.add("fileManager");
            addScript.add("product/form");
        
        } else if (mode.equals("category")) {
            pageTitle = "상품 분류";
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("subMenuCode", mode);
    }
}
