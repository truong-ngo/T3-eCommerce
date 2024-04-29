package com.t3e.authorizationserver.service.core;

import com.t3e.authorizationserver.model.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Manage {@link User}
 * */
public interface UserService extends UserDetailsManager {
    Optional<User> findByEmail(String email);
    void resetPassword(String email);
    String register(User user, BindingResult bindingResult, RedirectAttributes redirectAttributes);
    String forgotPassword(User user, RedirectAttributes redirectAttributes);
}
