package org.choongang.mypage.controllers;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestProfile {
    @NotBlank
    private String name;
    private String password;
    private String confirmPassword;
}
