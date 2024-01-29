package org.choongang.mypage.controllers;

import lombok.Data;

@Data
public class RequestResign {
    private String password;
    private String confirmPassword;
    private String authCode;
}
