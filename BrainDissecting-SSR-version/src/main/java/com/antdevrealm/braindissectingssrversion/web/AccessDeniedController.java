package com.antdevrealm.braindissectingssrversion.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

    @GetMapping("/access-denied")
    public String viewAccessDenied() {
        return "access-denied";
    }
}
