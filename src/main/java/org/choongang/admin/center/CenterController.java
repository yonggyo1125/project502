package org.choongang.admin.center;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.choongang.admin.menus.Menu;
import org.choongang.admin.menus.MenuDetail;
import org.choongang.center.controllers.CenterSearch;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.choongang.center.service.CenterSaveService;
import org.choongang.commons.ExceptionProcessor;
import org.choongang.commons.ListData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin/center")    //url 하나당 하나의 컨트롤러에 매핑되는 다른 핸들러 매핑과 달리 메서드 단위까지 세분화하여 적용 갸능. url, 파라미터, 헤더 등.
@RequiredArgsConstructor
public class CenterController implements ExceptionProcessor {

    private final CenterInfoService centerInfoService;
    private final CenterSaveService centerSaveService;


    @ModelAttribute("menuCode") //getMenyCode의 리턴값을 Model 객체와 바인딩
    public String getMenuCode() {
        return "center";
    }

    @ModelAttribute("subMenus")
    public List<MenuDetail> getSubMenus() {

        return Menu.getMenus("center");
    }

    /**
     * 센터 목록
     */
    @GetMapping
    public String list(@ModelAttribute CenterSearch search, Model model) {   // Model 객체: Controller에서 생성된 데이터를 담아 View로 전달할 때 사용
        commonProcess("list", model);

        ListData<CenterInfo> data = centerInfoService.getList(search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return "admin/center/list";
    }

    @PatchMapping
    public String editList(
            @RequestParam(name="chk", required = false) List<Integer> chks, Model model) {

        centerSaveService.saveList(chks);

        model.addAttribute("script", "parent.location.reload()");
        return "common/_execute_script";
    }

    /**
     * 센터 등록 - 등록 & 수정 통합 개발
     * @param model
     * @return
     */
    @GetMapping("/add_center")
    public String addCenter(@ModelAttribute RequestCenter form, Model model) {  // Model 객체를 통해 form 파라미터의 값들을 Getter, Setter, 생성자를 통해 주입, 전달
        commonProcess("add_center", model);

        return "admin/center/add_center";
    }

    /**
     * 센터 수정
     * @param model
     * @return
     */
    @GetMapping("/edit_center/{cCode}")
    public String editCenter(@PathVariable("cCode") Long cCode, Model model) {  // Model 객체를 통해 form 파라미터의 값들을 Getter, Setter, 생성자를 통해 주입, 전달
        commonProcess("edit_center", model);

        RequestCenter form = centerInfoService.getForm(cCode);

        model.addAttribute("requestCenter", form);

        return "admin/center/edit_center";
    }

    /**
     * 센터 추가, 저장
     * @param model
     * @return
     */
    @PostMapping("/save_center")
    public String saveCenter(@Valid RequestCenter form, Errors errors, Model model) {
        String mode = form.getMode();

        commonProcess(mode, model);

        if (errors.hasErrors()) {
            return "admin/center/" + mode;
        }

        CenterInfo data = centerSaveService.save(form);

        return "redirect:/admin/center/info_center/" + data.getCCode();
    }

    @GetMapping("/info_center/{cCode}")
    public String infoCenter(@PathVariable("cCode") Long cCode, Model model) {
        commonProcess("info_center", model);

        CenterInfo data = centerInfoService.get(cCode);

        model.addAttribute("centerInfo", data);

        return "admin/center/info_center";
    }

    /**
     * 공통 처리
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        String pageTitle = "헌혈의집 센터 목록";
        mode = Objects.requireNonNullElse(mode, "list");

        List<String> addCommonScript = new ArrayList<>();

        if (mode.equals("add_center") || mode.equals("edit_center")) {
            pageTitle = "새로운 센터 ";
            pageTitle += mode.contains("edit") ? "수정" : "등록";
            addCommonScript.add("address");

        } else if (mode.equals("info_center")) {
            pageTitle = "센터 상세 정보";

        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("addCommonScript", addCommonScript);
    }
}