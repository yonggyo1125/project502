package org.choongang.reservation.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.choongang.calendar.Calendar;
import org.choongang.commons.ExceptionProcessor;
import org.choongang.commons.Utils;
import org.choongang.reservation.entities.Reservation;
import org.choongang.reservation.service.ReservationApplyService;
import org.choongang.reservation.service.ReservationDateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reservation")
@RequiredArgsConstructor
@SessionAttributes({"requestReservation", "availableTimes"})
public class ReservationController implements ExceptionProcessor {

    private final ReservationValidator reservationValidator;
    private final ReservationApplyService reservationApplyService;
    private final ReservationDateService reservationDateService;
    private final Calendar calendar;
    private final Utils utils;

    private final HttpServletRequest request;

    @ModelAttribute("requestReservation")
    public RequestReservation requestReservation() {

        return new RequestReservation();
    }

    @ModelAttribute("addCss")
    public String[] addCss() {

        return new String[] { "reservation/style" };
    }


    @ModelAttribute("addScript")
    public String[] addScript() {
        return new String[] { "reservation/reservation" };
    }


    /**
     * 일정 선택 : 달력 노출
     *
     * @param cCode
     * @param form
     * @param model
     * @return
     */
    @GetMapping("/step1/{cCode}")
    public String step1(@PathVariable("cCode") Long cCode, @ModelAttribute RequestReservation form, Model model) {

        form.setCCode(cCode);
        form.setMode("step1");
        model.addAttribute("requestReservation", form);

        Map<String, Object> data = calendar.getData(form.getYear(), form.getMonth());
        model.addAllAttributes(data);

        return utils.tpl("reservation/step1");
    }

    /**
     * 방문 시간 선택
     *
     * @param form
     * @param model
     * @return
     */
    @PostMapping("/step2")
    public String step2(RequestReservation form, Errors errors, Model model) {

        reservationValidator.validate(form, errors);

        if (errors.hasErrors()) {
            Map<String, Object> data = calendar.getData(form.getYear(), form.getMonth());
            model.addAllAttributes(data);
            return utils.tpl("reservation/step1");
        }

        form.setMode("step2");

        // 검증 성공시 -> 예약 시간대 블록 조회
        List<LocalTime> availableTimes = reservationDateService.getAvailableTimes(form.getCCode(), form.getDate());

        model.addAttribute("availableTimes", availableTimes);

        return utils.tpl("reservation/step2");
    }

    @PostMapping("/apply")
    public String apply(RequestReservation form, Errors errors, Model model, SessionStatus status) {

        reservationValidator.validate(form, errors);
        System.out.println(form);

        if (errors.hasErrors()) {
            return utils.tpl("reservation/step2");
        }

        // 예약 신청 처리
        Reservation reservation = reservationApplyService.apply(form);

        status.setComplete(); // 세션 비우기

        String url = request.getContextPath() + "/reservation/" + reservation.getBookCode();
        String script = String.format("parent.location.replace('%s');", url);

        model.addAttribute("script", script);

        return "common/_execute_script";
    }
}
