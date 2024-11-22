package com.antdevrealm.braindissectingssrversion.web;

import com.antdevrealm.braindissectingssrversion.exception.NewUsernameConfirmUsernameException;
import com.antdevrealm.braindissectingssrversion.exception.UserNotFoundException;
import com.antdevrealm.braindissectingssrversion.exception.UsernameOrEmailException;
import com.antdevrealm.braindissectingssrversion.model.dto.user.UpdateDTO;
import com.antdevrealm.braindissectingssrversion.model.security.BrDissectingUserDetails;
import com.antdevrealm.braindissectingssrversion.service.ArticleService;
import com.antdevrealm.braindissectingssrversion.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {


    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final ArticleService articleService;

    public UserController(UserService userService, ArticleService articleService) {
        this.userService = userService;
        this.articleService = articleService;
    }


    @ModelAttribute("updateData")
    public UpdateDTO updateDTO() {
        return new UpdateDTO();
    }


    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails,
                              Model model) {

        model.addAttribute("user", brDissectingUserDetails);
        return "my-profile";
    }

    @PatchMapping("/profile/update")
    public String update(@AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails,
                         @Valid UpdateDTO updateDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("updateData", updateDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateData", bindingResult);

            redirectAttributes.addFlashAttribute("editProfile", true);
            return "redirect:/users/profile";
        }

        try {
            userService.update(brDissectingUserDetails.getId(), updateDTO);
        } catch (UserNotFoundException e) {
            log.error("Critical error: User not found during profile update. Logging out.");
            return "redirect:/users/logout";
        } catch (UsernameOrEmailException | NewUsernameConfirmUsernameException e) {
            if (e instanceof UsernameOrEmailException) {
                redirectAttributes.addFlashAttribute("usernameOrEmailTaken", e.getMessage());
            }
            if (e instanceof NewUsernameConfirmUsernameException) {
                redirectAttributes.addFlashAttribute("usernameConfUsernameMissMatch", e.getMessage());
            }

            redirectAttributes.addFlashAttribute("updateData", updateDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.updateData", bindingResult);
            redirectAttributes.addFlashAttribute("editProfile", true);
            return "redirect:/users/profile";

        }

        redirectAttributes.addFlashAttribute("successMessage", "You successfully updated your profile info!");

        return "redirect:/users/profile";
    }

    @GetMapping("/favourites")
    public String viewFavourites(Model model,
                                 @AuthenticationPrincipal
                                 BrDissectingUserDetails brDissectingUserDetails) {

        model.addAttribute("favourites", articleService.getUserFavourites(brDissectingUserDetails.getId()));
        model.addAttribute("currentUserId", brDissectingUserDetails.getId());

        return "user-favorites";
    }

    @PostMapping("/add-to-favourites/{articleId}")
    public String addToFavourites(@PathVariable Long articleId,
                                  @AuthenticationPrincipal BrDissectingUserDetails brDissectingUserDetails) {

        userService.addArticleToFavourites(articleId, brDissectingUserDetails.getId());
        return "redirect:/articles/all";
    }

    @DeleteMapping("/remove-from-favourites/{articleId}")
    public String removeFromFavourites(@PathVariable Long articleId,
                                       @AuthenticationPrincipal
                                       BrDissectingUserDetails brDissectingUserDetails) {

        userService.removeFromFavourites(articleId, brDissectingUserDetails.getId());

        return "redirect:/users/favourites";
    }

    @GetMapping("/banned")
    public String viewBanned() {
        return "account-banned";
    }


}
