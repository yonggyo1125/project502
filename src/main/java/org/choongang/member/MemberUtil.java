package org.choongang.member;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class MemberUtil {

    public static void clearLoginData(HttpSession session) {
        session.removeAttribute("username");
        session.removeAttribute("NotBlank_username");
        session.removeAttribute("NotBlank_password");
        session.removeAttribute("Global_error");
    }

}
