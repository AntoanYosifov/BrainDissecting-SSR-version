package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.dto.UserRegistrationDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/users")
public class UserController {

    @GetMapping("/register")
    public ModelAndView viewRegister(ModelAndView modelAndView){

        modelAndView.setViewName("auth-register");
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView doRegister(ModelAndView modelAndView, UserRegistrationDTO userRegistrationDTO){
        modelAndView.setViewName("auth-register");
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView viewLogin(ModelAndView modelAndView){
        modelAndView.setViewName("auth-login");
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView doLogin(ModelAndView modelAndView){
        // TODO:

        modelAndView.setViewName("auth-login");
        return modelAndView;
    }


}
