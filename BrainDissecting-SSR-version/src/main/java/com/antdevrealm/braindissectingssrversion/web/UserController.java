package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.exception.RegistrationUsernameOrEmailException;
import com.antdevrealm.braindissectingssrversion.model.dto.user.LoginDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {


    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("loginData")
    public LoginDTO loginDTO() {
        return new LoginDTO();
    }

    @ModelAttribute("registerData")
    public RegistrationDTO RegistrationDTO() {
        return new RegistrationDTO();
    }


    @GetMapping("/register")
    public String viewRegister() {
        return "auth-register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid RegistrationDTO registrationDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {


        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerData", registrationDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerData", bindingResult);
            return "redirect:/users/register";
        }

        try {
            userService.register(registrationDTO);
        } catch (RegistrationUsernameOrEmailException e) {

            log.error(e.getMessage());

            redirectAttributes.addFlashAttribute("usernameOrEmailTaken", e.getMessage());

            redirectAttributes.addFlashAttribute("registerData", registrationDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerData", bindingResult);

            return "redirect:/users/register";
        }


        return "redirect:/users/login";

    }

    @GetMapping("/login")
    public String viewLogin() {
        return "auth-login";
    }

    @PostMapping("/login")
    public String doLogin(@Valid LoginDTO loginDTO,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("loginData", loginDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginData", bindingResult);

            return "redirect:/users/login";
        }

        return "forward:/users/perform-login";
    }

    @GetMapping("/profile")
    public String details(@AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails,
                          Model model) {

        model.addAttribute("user", brDissectingUserDetails);
        return "user-profile";
    }

}
