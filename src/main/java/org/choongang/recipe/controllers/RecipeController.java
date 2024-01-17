package org.choongang.recipe.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.choongang.commons.ExceptionProcessor;
import org.choongang.commons.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController implements ExceptionProcessor {
    private final Utils utils;

    @GetMapping("/write")
    public String write(@ModelAttribute RequestRecipe form, Model model) {
        commonProcess("add", model);
        return utils.tpl("recipe/add");
    }

    @PostMapping("/save")
    public String save(@Valid RequestRecipe form, Errors errors, Model model) {
        String mode = form.getMode();
        commonProcess(mode, model);

        if (errors.hasErrors()) {
            return utils.tpl("recipe/" + mode);
        }


        return "redirect:/recipe"; // 레서피 목록
    }

    private void commonProcess(String mode, Model model) {

    }
}
