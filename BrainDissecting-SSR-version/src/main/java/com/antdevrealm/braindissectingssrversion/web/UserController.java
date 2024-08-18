package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.dto.user.LoginDTO;
import com.antdevrealm.braindissectingssrversion.model.dto.user.RegistrationDTO;
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
    public RegistrationDTO RegistrationDTO() {
        return new RegistrationDTO();
    }

    @ModelAttribute("loginData")
    public LoginDTO loginDTO() {
        return new LoginDTO();
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
            return "redirect:/users/register";
        }

        boolean success = userService.register(registrationDTO);
        // TODO: Handle errors based on the user registration success
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String viewLogin() {
        return "auth-login";
    }

    @PostMapping("/login")
    public String doLogin(@Valid LoginDTO loginDTO) {
        // TODO:
        boolean success = userService.login(loginDTO);

        if(!success) {
            return "redirect:/users/login";
        }

        return "redirect:/articles/all";
    }


}
