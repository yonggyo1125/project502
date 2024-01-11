package org.choongang.admin.product.controllers;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CategoryValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(RequestCategory.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        /* cateCd : 분류 코드 중복 여부 체크 */
        RequestCategory form = (RequestCategory)target;
        String cateCd = form.getCateCd();


    }
}
