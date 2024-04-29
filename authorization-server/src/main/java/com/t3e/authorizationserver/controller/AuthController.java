package com.t3e.authorizationserver.controller;

import com.t3e.authorizationserver.model.User;
import com.t3e.authorizationserver.service.core.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @ModelAttribute
    public void addAttribute(Model model) {
        model.addAttribute("user", new User());
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid User user, BindingResult result, RedirectAttributes redirectAttributes) {
        return userService.register(user, result, redirectAttributes);
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        return userService.forgotPassword(user, redirectAttributes);
    }
}
