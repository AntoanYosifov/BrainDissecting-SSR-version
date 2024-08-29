package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home (@AuthenticationPrincipal UserDetails userDetails, Model model){

        if (userDetails instanceof BrDissectingUserDetails brDissectingUserDetails) {
            model.addAttribute("welcomeMessage", brDissectingUserDetails.getFullName());
        } else {
            model.addAttribute("welcomeMessage", "Anonymous");
        }
        return "index";
    }
}
