package com.t3e.authorizationserver.service.impl;

import com.t3e.authorizationserver.model.User;
import com.t3e.authorizationserver.repository.UserRepository;
import com.t3e.authorizationserver.repository.UserSpecification;
import com.t3e.authorizationserver.service.core.UserService;
import com.t3e.authorizationserver.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Implementation of {@link UserService}
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final UserSpecification specification;

    @Override
    public void createUser(UserDetails user) {
        repository.save(mapper.toEntity((User) user));
    }

    @Override
    public void updateUser(UserDetails user) {
        repository.save(mapper.toEntity((User) user));
    }

    @Override
    public void deleteUser(String username) {
        repository.delete(specification.filter(specification.getUsernameEqualFilter(username)));
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
    public boolean userExists(String username) {
        return repository.exists(specification.filter(specification.getUsernameEqualFilter(username)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findOne(specification.filter(specification.getUsernameEqualFilter(username))).map(mapper::toDto).orElseThrow(() -> new UsernameNotFoundException("Not found user with username: " + username));
    }
}
