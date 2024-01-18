package org.choongang.center.service;

import org.choongang.commons.Utils;
import org.choongang.commons.exceptions.AlertBackException;
import org.springframework.http.HttpStatus;

public class CenterNotFoundException extends AlertBackException {
    public CenterNotFoundException() {
        super(Utils.getMessage("NotFound.center", "errors"), HttpStatus.NOT_FOUND);
    }
}