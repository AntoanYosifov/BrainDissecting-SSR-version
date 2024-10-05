package com.antdevrealm.braindissectingssrversion.web;


import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.AdminService;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final ArticleService articleService;

    public AdminController(AdminService adminService, UserService userService, ArticleService articleService) {
        this.adminService = adminService;
        this.userService = userService;
        this.articleService = articleService;

    }


    @GetMapping("/manage-roles")
    public String viewAdminManage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "manage-users";
    }

    @PostMapping("/promote-moderator/{userId}")
    public String promoteToModerator(@PathVariable Long userId,
                                     RedirectAttributes redirectAttributes,
                                     @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {
        if (!adminService.promoteToModerator(userId)) {
            redirectAttributes.addFlashAttribute("roleAssignFailure", "Failed to assign role!");
            return "redirect:/admin/manage-roles";
        }

        userService.reloadUserDetails(brDissectingUserDetails.getUsername());

        redirectAttributes.addFlashAttribute("roleAssignSuccess", "Role assigned successfully!");
        return "redirect:/admin/manage-roles";

    }

    @PostMapping("/demote-moderator/{userId}")
    public String demoteToModerator(@PathVariable Long userId,
                                    RedirectAttributes redirectAttributes,
                                    @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {

        if (!adminService.demoteFromModerator(userId)) {
            redirectAttributes.addFlashAttribute("removeRoleFailure", "Failed to remove role!");
            return "redirect:/admin/manage-roles";
        }

        userService.reloadUserDetails(brDissectingUserDetails.getUsername());

        redirectAttributes.addFlashAttribute("removeRoleSuccess", "Role removed successfully");
        return "redirect:/admin/manage-roles";

    }

    @GetMapping("/delete-article")
    public String viewDeleteArticle(Model model) {
        // TODO: Add rating functionality to the articles and sort them
        model.addAttribute("articles", articleService.getAllApproved());
        return "delete-article";
    }

    @DeleteMapping("/delete-article/{articleId}")
    public String deleteArticle(@PathVariable Long articleId) {
        articleService.deleteArticle(articleId);
        return "redirect:/admin/delete-article";
    }

    @PostMapping("/ban-user/{userId}")
    public String banUser(@PathVariable Long userId,
                          RedirectAttributes redirectAttributes) {
        if (!adminService.banUser(userId)) {
            redirectAttributes.addFlashAttribute("BanFailure", "BAN operation has failed!");
            return "redirect:/admin/manage-roles";
        }

        redirectAttributes.addFlashAttribute("BanSuccess", "BAN operation successful!");
        return "redirect:/admin/manage-roles";

    }

    @PostMapping("/remove-ban/{userId}")
    public String removeBan(@PathVariable Long userId,
                            RedirectAttributes redirectAttributes) {

        if (!adminService.removeBan(userId)) {
            redirectAttributes.addFlashAttribute("removeBanFailure", "Failed to remove BAN!");
            return "redirect:/admin/manage-roles";
        }

        redirectAttributes.addFlashAttribute("removeBanSuccess", "BAN removed successfully");
        return "redirect:/admin/manage-roles";
    }

    @GetMapping("/manage-themes")
    public String manageThemes(Model model) {
        model.addAttribute("themes", articleService.getThemes());
        return "manage-themes";
    }

    @PostMapping("/add-theme")
    public String addTheme(@RequestParam String theme) {
        articleService.addTheme(theme);
        return "redirect:/admin/manage-themes";
    }

    @DeleteMapping("/remove-theme")
    public String removeTheme(@RequestParam String theme) {
        articleService.removeTheme(theme);
        return "redirect:/admin/manage-themes";
    }

}


