package org.choongang.center.service;

import lombok.RequiredArgsConstructor;
import org.choongang.admin.center.RequestCenter;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.repositories.CenterInfoRepository;
import org.choongang.commons.Utils;
import org.choongang.commons.exceptions.AlertException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CenterSaveService {

    private final CenterInfoRepository infoRepository;
    private final Utils utils;

    public CenterInfo save(RequestCenter form) {
        String mode = form.getMode();
        Long cCode = form.getCCode();

        mode = StringUtils.hasText(mode) ? mode : "add_center";

        CenterInfo data = null;
        if (mode.equals("edit_center") && cCode != null) {
            data = infoRepository.findBycCode(cCode).orElseThrow(CenterNotFoundException::new);
        } else {
            data = new CenterInfo();
        }

        data.setCName(form.getCName());
        data.setZonecode(form.getZonecode());
        data.setAddress(form.getAddress());
        data.setAddressSub(form.getAddressSub());
        data.setTelNum(form.getTelNum());
        data.setOperHour(form.getOperHour());

        // 예약 요일  월,화...
        List<String> bookYoil = form.getBookYoil();
        String bookYoilStr = bookYoil == null ? null
                                    : bookYoil.stream().collect(Collectors.joining(","));

        data.setBookYoil(bookYoilStr);

        //  예약 가능 시간 09:00-18:00
        String bookAvl = String.format("%s:%s-%s:%s",
                    form.getBookAvlShour(),
                    form.getBookAvlSmin(), form.getBookAvlEhour(), form.getBookAvlEmin());
        data.setBookAvl(bookAvl);
        data.setBookNotAvl(form.getBookNotAvl());
        data.setBookHday(form.isBookHday());
        data.setBookBlock(form.getBookBlock());
        data.setBookCapacity(form.getBookCapacity());

        infoRepository.saveAndFlush(data);

        return data;
    }

    /**
     * 목록 수정
     *
     * @param chks
     */
    public void saveList(List<Integer> chks) {
        if (chks == null || chks.isEmpty()) {
            throw new AlertException("수정할 센터를 선택하세요.", HttpStatus.BAD_REQUEST);
        }

        for (int chk : chks) {
            Long cCode = Long.valueOf(utils.getParam("cCode_" + chk));
            CenterInfo data = infoRepository.findById(cCode).orElse(null);
            if (data == null) continue;

            String bookYoil = Arrays.stream(utils.getParams("bookYoil_" + chk)).collect(Collectors.joining(",")); // 월,화,수
            data.setBookYoil(bookYoil);


            String bookAvl = String.format("%s:%s-%s:%s",
                    utils.getParam("bookAvlShour_" + chk),
                    utils.getParam("bookAvlSmin_" + chk),
                    utils.getParam("bookAvlEhour_" + chk),
                    utils.getParam("bookAvlEmin_" + chk));
            data.setBookAvl(bookAvl);

            boolean bookHday = Boolean.parseBoolean(utils.getParam("bookHday_" + chk));
            data.setBookHday(bookHday);

            int bookBlock = Integer.parseInt(utils.getParam("bookBlock_" + chk));
            data.setBookBlock(bookBlock);
        }

        infoRepository.flush();
    }
}
