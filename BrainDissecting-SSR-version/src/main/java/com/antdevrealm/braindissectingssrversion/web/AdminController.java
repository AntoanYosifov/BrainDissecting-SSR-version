package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.exception.RoleNotFoundException;
import com.antdevrealm.braindissectingssrversion.exception.UserNotFoundException;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.AdminService;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.ThemeSuggestionService;
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
    private final ThemeSuggestionService themeSuggestionService;

    public AdminController(AdminService adminService,
                           UserService userService,
                           ArticleService articleService, ThemeSuggestionService themeSuggestionService) {
        this.adminService = adminService;
        this.userService = userService;
        this.articleService = articleService;
        this.themeSuggestionService = themeSuggestionService;
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
        try {
            adminService.promoteToModerator(userId);
            userService.reloadUserDetails(brDissectingUserDetails.getUsername());
            redirectAttributes.addFlashAttribute("roleAssignSuccess", "Role assigned successfully!");
            return "redirect:/admin/manage-roles";
        } catch (UserNotFoundException | RoleNotFoundException e) {
            redirectAttributes.addFlashAttribute("roleAssignFailure", "Failed to assign role!");
            return "redirect:/admin/manage-roles";
        }

    }

    @PostMapping("/demote-moderator/{userId}")
    public String demoteFromModerator(@PathVariable Long userId,
                                      RedirectAttributes redirectAttributes,
                                      @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {

        try {
            adminService.demoteFromModerator(userId);

            userService.reloadUserDetails(brDissectingUserDetails.getUsername());

            redirectAttributes.addFlashAttribute("removeRoleSuccess", "Role removed successfully!");
            return "redirect:/admin/manage-roles";
        } catch (UserNotFoundException | RoleNotFoundException e) {
            redirectAttributes.addFlashAttribute("removeRoleFailure", "Failed to remove role!");
            return "redirect:/admin/manage-roles";
        }
    }

    @GetMapping("/delete-article")
    public String viewDeleteArticle(Model model) {
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
        model.addAttribute("suggestedThemes", themeSuggestionService.getAll());
        return "manage-themes";
    }

    @PostMapping("/add-theme")
    public String addTheme(@RequestParam String theme) {
        boolean isAdded = articleService.addTheme(theme);

        if (!isAdded) {
            return "redirect:/admin/manage-themes?error=Adding theme operation failed!";
        }

        return "redirect:/admin/manage-themes?success=Theme added!";
    }

    @DeleteMapping("/remove-theme")
    public String removeTheme(@RequestParam String theme) {
        boolean isRemoved = articleService.removeTheme(theme);

        if (!isRemoved) {
            return "redirect:/admin/manage-themes?error=Remove theme operation failed!";
        }

        return "redirect:/admin/manage-themes?success=Theme removed!";
    }

    @PostMapping("/approve-theme")
    public String approveSuggestedTheme(@RequestParam Long themeId) {
        themeSuggestionService.approveTheme(themeId);

        return "redirect:/admin/manage-themes";
    }

    @DeleteMapping("/reject-theme")
    public String rejectSuggestedTheme(@RequestParam Long themeId) {
        themeSuggestionService.rejectTheme(themeId);
        return "redirect:/admin/manage-themes";
    }

    @PostMapping("/update-articles")
    public String manualUpdate() {
        boolean updated = articleService.updateArticles();

        if (!updated) {
            return "redirect:/admin/manage-themes?error=Failed to update Articles! Please try again later!";
        }

        return "redirect:/admin/manage-themes?success=Articles updated successfully!";
    }

}


