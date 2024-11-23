package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.ModeratorService;
import com.antdevrealm.braindissectingssrversion.service.ThemeSuggestionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/moderator")
public class ModeratorController {

    private final ArticleService articleService;
    private final ModeratorService moderatorService;
    private final ThemeSuggestionService themeSuggestionService;

    public ModeratorController(ArticleService articleService, ModeratorService moderatorService, ThemeSuggestionService themeSuggestionService) {
        this.articleService = articleService;
        this.moderatorService = moderatorService;
        this.themeSuggestionService = themeSuggestionService;
    }

    @GetMapping("/pending-for-approval")
    public String viewApproveArticles(Model model) {
        model.addAttribute("pendingArticles", articleService.getAllPending());
        return "pending-for-approval";
    }

    @PatchMapping("/approve/{articleId}")
    public String approveArticle(@PathVariable Long articleId) {
        boolean isApproved = moderatorService.approveArticle(articleId);

        if (!isApproved) {
            return "redirect:/moderator/pending-for-approval?error=Could not approve the article";
        }

        return "redirect:/moderator/pending-for-approval?success=Article approved";
    }

    @DeleteMapping("/reject/{articleId}")
    public String rejectArticle(@PathVariable Long articleId) {
        boolean isRejected = moderatorService.rejectArticle(articleId);

        if (!isRejected) {
            return "redirect:/moderator/pending-for-approval?error=Could not reject the article";
        }

        return "redirect:/moderator/pending-for-approval?success=Article rejected";
    }

    @GetMapping("/suggest-themes")
    public String viewSuggestThemes(Model model,
                                    @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {
        model.addAttribute("suggestedThemes", themeSuggestionService.getAllModeratorSuggestions(brDissectingUserDetails.getId()));
        return "suggest-themes";
    }

    @PostMapping("/suggest-theme")
    public String suggestTheme(@RequestParam String theme,
                               @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails,
                               RedirectAttributes redirectAttributes) {
        boolean isSuggested = themeSuggestionService.suggestTheme(theme, brDissectingUserDetails.getId());

        if (!isSuggested) {
            return "redirect:/moderator/suggest-themes?error=Failed to suggest theme. Please try to suggest a different theme.";
        }

        return "redirect:/moderator/suggest-themes?success=Theme suggested successfully.";
    }

}
