package org.choongang.admin.reservation.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.choongang.admin.menus.Menu;
import org.choongang.admin.menus.MenuDetail;
import org.choongang.center.controllers.CenterSearch;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.choongang.commons.ExceptionProcessor;
import org.choongang.commons.ListData;
import org.choongang.reservation.controllers.RequestReservation;
import org.choongang.reservation.controllers.ReservationSearch;
import org.choongang.reservation.entities.Reservation;
import org.choongang.reservation.service.ReservationDeleteService;
import org.choongang.reservation.service.ReservationInfoService;
import org.choongang.reservation.service.ReservationSaveService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller("adminReservationController")
@RequestMapping("/admin/reservation")
@RequiredArgsConstructor
public class ReservationController implements ExceptionProcessor {

    private final ReservationInfoService reservationInfoService;
    private final ReservationSaveService reservationSaveService;
    private final ReservationDeleteService reservationDeleteService;
    private final CenterInfoService centerInfoService;

    @ModelAttribute("menuCode")
    public String getMenuCode() {
        return "reservation";
    }

    @ModelAttribute("subMenus")
    public List<MenuDetail> getSubMenus() {
        return Menu.getMenus("reservation");
    }

    /**
     * 예약 현황 / 예약 관리
     */
    @GetMapping
    public String list(@ModelAttribute ReservationSearch search, Model model) {
        commonProcess("list", model);

        ListData<Reservation> data = reservationInfoService.getList(search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return "admin/reservation/list";
    }

    @PatchMapping
    public String editList(@RequestParam(name="chk", required = false) List<Integer> chks, Model model) {
        commonProcess("list", model);

        reservationSaveService.saveList(chks);

        model.addAttribute("script", "parent.location.reload();");
        return "common/_execute_script";
    }

    @DeleteMapping
    public String deleteList(@RequestParam(name="chk", required = false) List<Integer> chks, Model model) {
        commonProcess("list", model);

        reservationDeleteService.deleteList(chks);

        model.addAttribute("script", "parent.location.reload();");
        return "common/_execute_script";
    }

    @GetMapping("/edit/{bookCode}")
    public String edit(@PathVariable("bookCode") Long bookCode, Model model) {
        commonProcess("edit", model);

        RequestReservation form = reservationInfoService.getForm(bookCode);
        model.addAttribute("requestReservation", form);

        CenterInfo center = centerInfoService.get(form.getCCode());
        model.addAttribute("center", center);

        CenterSearch search = new CenterSearch();
        search.setLimit(10000);
        ListData<CenterInfo> data = centerInfoService.getList(search);
        List<CenterInfo> centerList = data.getItems();
        model.addAttribute("centerList", data.getItems());

        return "admin/reservation/edit";
    }

    @PostMapping("/save")
    public String save(@Valid RequestReservation form, Errors errors, Model model) {
        commonProcess("edit", model);

        if (errors.hasErrors()) {
            return "admin/reservation/edit";
        }

        reservationSaveService.save(form);

        return "redirect:/admin/reservation";
    }

    /**
     * 지점 등록
     * @param model
     * @return
     */
    @GetMapping("/add_branch")
    public String addBranch(@ModelAttribute RequestBranch form, Model model) {
        commonProcess("add_branch", model);

        return "admin/reservation/add_branch";
    }

    /**
     * 브랜치 추가, 저장
     * @param model
     * @return
     */
    @PostMapping("/save_branch")
    public String saveBranch(@Valid RequestBranch form, Errors errors, Model model) {
        String mode = form.getMode();
        commonProcess(mode, model);

        if (errors.hasErrors()) {
            return "admin/reservation/" + mode;
        }

        return "redirect:/admin/reservation/branch";
    }

    @GetMapping("/branch")
    public String branchList(Model model) {
        commonProcess("branch", model);

        return "admin/reservation/branch_list";
    }

    /**
     * 공통 처리
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        String pageTitle = "예약 현황";
        mode = Objects.requireNonNullElse(mode, "list");

        List<String> addScript = new ArrayList<>();
        addScript.add("reservation/common");

        if (mode.equals("add_branch")) {
            pageTitle = "지점 등록";
            
        } else if (mode.equals("edit_branch")) {
            pageTitle = "지점 수정";
        } else if (mode.equals("branch")) {
            pageTitle = "지점 목록";
        } else if (mode.equals("edit")) {
            pageTitle = "예약 정보 수정";

        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("subMenuCode", mode);
        model.addAttribute("addScript", addScript);
    }
}