package org.choongang.mypage.controllers;

import lombok.Data;

@Data
public class RequestResign {
    private String mode = "step1";
    private String password;
    private String confirmPassword;
    private Integer authCode;
}
