package com.antdevrealm.braindissectingssrversion.web;


import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.AdminService;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import com.antdevrealm.braindissectingssrversion.service.impl.BrDissectingUserDetailService;
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
        return "manage-roles";
    }

    @PostMapping("/promote-moderator/{userId}")
    public String promoteToModerator(@PathVariable Long userId,
                                     RedirectAttributes redirectAttributes,
                                     @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {
        if(!adminService.promoteToModerator(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage",  "Failed to assign role!");
            return "redirect:/admin/manage-roles";
        }

        userService.reloadUserDetails(brDissectingUserDetails.getUsername());

        redirectAttributes.addFlashAttribute("successMessage", "Role assigned successfully!");
        return "redirect:/admin/manage-roles";

    }

    @PostMapping("/demote-moderator/{userId}")
    public String demoteToModerator(@PathVariable Long userId,
                                     RedirectAttributes redirectAttributes,
                                    @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {
        if(!adminService.demoteFromModerator(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage",  "Failed to remove role!");
            return "redirect:/admin/manage-roles";
        }

        userService.reloadUserDetails(brDissectingUserDetails.getUsername());

        redirectAttributes.addFlashAttribute("successMessage", "Role removed successfully");
        return "redirect:/admin/manage-roles";

    }

    @GetMapping("/delete-article")
    public String viewDeleteArticle(Model model) {
        // TODO: Display info about number of comments / upload date
        // TODO: Add rating functionality to the articles and sort them
        model.addAttribute("articles", articleService.getAllArticles());
        return "delete-article";
    }

    @DeleteMapping("/delete-article/{articleId}")
    public String deleteArticle(@PathVariable Long articleId) {
        articleService.deleteArticle(articleId);

        return "redirect:/admin/delete-article";
    }

    @PostMapping("/ban-user/{userId}")
    public String banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return "redirect:/admin/manage-roles";
    }


}
