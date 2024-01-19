package org.choongang.center.service;

import lombok.RequiredArgsConstructor;
import org.choongang.admin.center.RequestCenter;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.repositories.CenterInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CenterSaveService {

    private final CenterInfoRepository infoRepository;

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
}
