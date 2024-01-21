package org.choongang.reservation.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ReservationValidator implements Validator {
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
        if (form.getDate() == null) {
            errors.rejectValue("date", "NonNull");
        }

    }

    /**
     * step2 검증
     *
     * @param form
     * @param errors
     */
    private void validateStep2(RequestReservation form, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "time", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personsInfo", "NotBlank");
        int persons = form.getPersons();
        if (persons < 1) {
            errors.rejectValue("persons", "Size");
        }
    }
}
