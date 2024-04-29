package com.t3e.authorizationserver.service.impl;

import com.t3e.authorizationserver.model.User;
import com.t3e.authorizationserver.repository.UserRepository;
import com.t3e.authorizationserver.repository.UserSpecification;
import com.t3e.authorizationserver.service.core.UserService;
import com.t3e.authorizationserver.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link UserService}
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${user.default_password}")
    private String defaultPassword;

    private final UserRepository repository;
    private final UserMapper mapper;
    private final UserSpecification specification;

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findOne(specification.filter(specification.getEmailEqualFilter(email))).map(mapper::toDto);
    }

    @Override
    public void createUser(UserDetails user) {
        repository.save(mapper.toEntity((User) user));
    }

    @Override
    public void updateUser(UserDetails user) {
        repository.save(mapper.toEntity((User) user));
    }

    @Override
    public void deleteUser(String email) {
        repository.delete(specification.filter(specification.getEmailEqualFilter(email)));
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        String username = currentUser.getName();
        log.info(String.format("Changing password for user '%s'", username));
        User user = (User) loadUserByUsername(username);
        Assert.state(user != null, "Current user doesn't exist in database.");
        Assert.state(user.getPassword().equals(oldPassword), "Old password not match.");
        user.setPassword(newPassword);
        repository.save(mapper.toEntity(user));
    }

    @Override
    public boolean userExists(String email) {
        return repository.exists(specification.filter(specification.getEmailEqualFilter(email)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Not found user with username: " + username));
    }

    @Override
    public void resetPassword(String email) {
        Optional<User> user = findByEmail(email);
        if (user.isPresent()) {
            user.get().setPassword(defaultPassword);
            updateUser(user.get());

            // Mail to user
        }
    }

    @Override
    public String register(User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (userExists(user.getUsername())) {
            redirectAttributes.addAttribute("email_error", "Email already existed");
            return "redirect:/register";
        }
        user.setRoles(List.of("ROLE_USER"));
        createUser(user);
        redirectAttributes.addFlashAttribute("message", "User created successfully, Please login");
        return "redirect:/login";
    }

    @Override
    public String forgotPassword(User user, RedirectAttributes redirectAttributes) {
        if (!userExists(user.getUsername())) {
            redirectAttributes.addAttribute("error_message", "Email: " + user.getUsername() + " not existed");
            return "redirect:/forgot-password";
        }
        resetPassword(user.getUsername());
        redirectAttributes.addAttribute("success_message", "New password has been sent to email: " + user.getUsername());
        return "redirect:/forgot-password";
    }
}
