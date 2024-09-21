package com.antdevrealm.braindissectingssrversion.web;


import com.antdevrealm.braindissectingssrversion.service.AdminService;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }


    @GetMapping("/manage-roles")
    public String viewAdminMenage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "manage-roles";
    }

    @PostMapping("/promote-moderator")
    public String promoteToModerator(@RequestParam Long userId,
                                     RedirectAttributes redirectAttributes) {
        if(!adminService.promoteToModerator(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage",  "Failed to assign role!");
            return "redirect:/admin/manage-roles";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Role assigned successfully!");
        return "redirect:/admin/manage-roles";

    }

    @PostMapping("/demote-moderator")
    public String demoteToModerator(@RequestParam Long userId,
                                     RedirectAttributes redirectAttributes) {
        if(!adminService.demoteFromModerator(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage",  "Failed to remove role!");
            return "redirect:/admin/manage-roles";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Role removed successfully");
        return "redirect:/admin/manage-roles";

    }

}
