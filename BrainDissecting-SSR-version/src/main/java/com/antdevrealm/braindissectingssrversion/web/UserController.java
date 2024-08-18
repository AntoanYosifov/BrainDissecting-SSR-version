package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.dto.user.UserRegistrationDTO;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("registerData")
    public UserRegistrationDTO userRegistrationDTO() {
        return new UserRegistrationDTO();
    }

    @GetMapping("/register")
    public String viewRegister() {
        return "auth-register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid UserRegistrationDTO userRegistrationDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {


        if(bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerData", userRegistrationDTO);
            return "redirect:/users/register";
        }

        boolean success = userService.register(userRegistrationDTO);
        // TODO:
        return "redirect:/";
    }

    @GetMapping("/login")
    public String viewLogin() {


        return "auth-login";
    }

    @PostMapping("/login")
    public String doLogin() {
        // TODO:

        return "auth-login";
    }


}
