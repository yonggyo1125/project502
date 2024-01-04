package org.choongang.commons;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.choongang.admin.config.service.ConfigInfoService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice("org.choongang")
@RequiredArgsConstructor
public class BasicConfigAdvice {
    private final ConfigInfoService infoService;

    @ModelAttribute("basicConfig")
    public Map<String, String> getBasicConfig() {
        Optional<Map<String, String>> opt = infoService.get("basic", new TypeReference<>() {});

        Map<String, String> config = opt.orElseGet(() -> new HashMap<String, String>());
        return config;
    }
}
