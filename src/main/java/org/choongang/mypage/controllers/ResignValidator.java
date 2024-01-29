package org.choongang.mypage.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.member.MemberUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ResignValidator implements Validator {

    private final MemberUtil memberUtil;
    private final PasswordEncoder encoder;

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestResign form = (RequestResign) target;
        String mode = form.getMode();
        mode = StringUtils.hasText(mode) ? mode : "step1";

        if (mode.equals("step2")) {
            validateStep2(form, errors);
        } else {
            validateStep1(form, errors);
        }

    }

    /**
     * 1. 비밀번호, 비밀번호 확인 - 필수 항목
     * 2. 비밀번호, 비밀번호 확인의 일치 여부
     * 3. 비밀번호와 회원 정보와 일치 여부
     * @param form
     * @param errors
     */
    private void validateStep1(RequestResign form, Errors errors) {
        String password = form.getPassword();
        String confirmPassword = form.getConfirmPassword();
    }

    /**
     * 1. 인증코드 필수 여부
     * 2. 인증코드가 맞는지 체크
     *
     * @param form
     * @param errors
     */
    private void validateStep2(RequestResign form, Errors errors) {

    }
}
