package org.choongang.admin.board.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.board.constants.BoardUseType;
import org.choongang.board.repositories.BoardRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class BoardConfigValidator implements Validator {

    private final BoardRepository boardRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestBoardConfig.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        /* 게시판 아이디 중복 체크 */
        RequestBoardConfig form = (RequestBoardConfig) target;

        String bid = form.getBid();
        String mode = form.getMode();
        if (mode.equals("add") && StringUtils.hasText(bid) && boardRepository.existsById(bid)) {
            errors.rejectValue("bid", "Duplicated");
        }

        /* 학교별 게시판이라면 학교선택 필수 */
        String useType = form.getUseType();
        String schoolDomain = form.getSchoolDomain();
        if (BoardUseType.SCHOOL == BoardUseType.valueOf(useType) && !StringUtils.hasText(schoolDomain)) {
            errors.rejectValue("schoolDomain", "NotBlank");
        }
    }
}
