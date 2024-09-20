package com.antdevrealm.braindissectingssrversion.web;


import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/manage-roles")
    public String viewAdminMenage(Model model) {

        model.addAttribute("users", userService.getAllUsers());
        return "manage-roles";
    }

}
