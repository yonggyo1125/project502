package org.choongang.admin.config.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.commons.ExceptionProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/config")
@RequiredArgsConstructor
public class BasicConfigController implements ExceptionProcessor {

    @ModelAttribute("menuCode")
    public String getMenuCode() {
        return "config";
    }

    @GetMapping
    public String index(@ModelAttribute BasicConfig config, Model model) {
        return "admin/config/basic";
    }

    @PostMapping
    public String save(BasicConfig config, Model model) {

        return "admin/config/basic";
    }
}
