package org.choongang.reservation.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.center.controllers.CenterSearch;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.service.CenterInfoService;
import org.choongang.commons.ExceptionRestProcessor;
import org.choongang.commons.ListData;
import org.choongang.commons.rests.JSONData;
import org.choongang.reservation.service.ReservationInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ApiReservationController implements ExceptionRestProcessor {

    private final ReservationInfoService reservationInfoService;
    private final CenterInfoService centerInfoService;

    @GetMapping("/available_capacity")
    public JSONData<Integer> availableCapacity(
            @RequestParam("cCode") Long cCode,
            @RequestParam("date") LocalDate date,
            @RequestParam("time") LocalTime time) {

        Integer capacity = reservationInfoService.getAvailableCapacity(cCode, LocalDateTime.of(date, time));

        return new JSONData<>(capacity);
    }

    @GetMapping("/center")
    public JSONData<List<CenterInfo>> getCenters(@RequestParam(name="skey", required = false) String skey) {

        CenterSearch search = new CenterSearch();
        search.setSopt("cName");
        search.setSkey(skey);
        search.setLimit(10000);

        ListData<CenterInfo> data = centerInfoService.getList(search);

        return new JSONData<>(data.getItems());
    }
}
