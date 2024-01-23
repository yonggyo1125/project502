package org.choongang.reservation.controllers;

import lombok.RequiredArgsConstructor;
import org.choongang.reservation.service.ReservationDateService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ReservationValidator implements Validator {

    private final ReservationDateService dateService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestReservation.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RequestReservation form = (RequestReservation)target;

        String mode = StringUtils.hasText(form.getMode()) ? form.getMode() : "step1";
        if (mode.equals("step2")) {
            validateStep2(form, errors);
        } else {
            validateStep1(form, errors);
        }

    }

    /**
     * step1 검증
     *
     * @param form
     * @param errors
     */
    private void validateStep1(RequestReservation form, Errors errors) {
        LocalDate date = form.getDate();
        Long cCode = form.getCCode();

        // 필수 항목
        if (date == null) {
            errors.rejectValue("date", "NonNull");
        }

        // 예약 가능일자 체크
        if (!dateService.checkAvailable(cCode, date.toString())) {
            errors.rejectValue("date", "NotAvailable");
        }

    }

    /**
     * step2 검증
     *
     * @param form
     * @param errors
     */
    private void validateStep2(RequestReservation form, Errors errors) {
       if (form.getTime() == null) {
           errors.rejectValue("time", "NonNull");
       }

        int persons = form.getPersons();
        if (persons < 1) {
            errors.rejectValue("persons", "Size");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "donorTel", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bookType", "NotBlank");
    }
}
