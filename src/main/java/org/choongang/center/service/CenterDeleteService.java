package org.choongang.center.service;

import lombok.RequiredArgsConstructor;
import org.choongang.center.entities.CenterInfo;
import org.choongang.center.repositories.CenterInfoRepository;
import org.choongang.commons.Utils;
import org.choongang.commons.exceptions.AlertException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CenterDeleteService {
    private final CenterInfoRepository infoRepository;
    private final Utils utils;

    public void deleteList(List<Integer> chks) {
        if (chks == null || chks.isEmpty()) {
            throw new AlertException("삭제할 센터를 선택하세요.", HttpStatus.BAD_REQUEST);
        }

        for (int chk : chks) {
            Long cCode = Long.valueOf(utils.getParam("cCode_" + chk));
            CenterInfo data = infoRepository.findById(cCode).orElse(null);
            if (data == null) continue;

            infoRepository.delete(data);
        }

        infoRepository.flush();
    }
}
