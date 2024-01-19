package org.choongang.center.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.choongang.commons.ExceptionProcessor;
import org.choongang.commons.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/center")
@RequiredArgsConstructor
public class CenterController implements ExceptionProcessor {

    private final CenterInfoService infoService;
    private final Utils utils;

    private CenterInfo centerInfo;

    @GetMapping("/{cCode}")
    public String view(@PathVariable("cCode") Long cCode, Model model) {
        commonProcess(cCode, "view", model);

        return utils.tpl("center/view");
    }

    private void commonProcess(String mode, Model model) {
        String pageTitle = "";
        if (mode.equals("view")) {
            pageTitle = centerInfo.getCName();
        }

        model.addAttribute("pageTitle", pageTitle);
    }

    private void commonProcess(Long cCode, String mode, Model model) {

        centerInfo = infoService.get(cCode);

        commonProcess(mode, model);

        model.addAttribute("centerInfo", centerInfo);
    }
}
