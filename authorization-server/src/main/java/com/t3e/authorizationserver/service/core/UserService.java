package com.t3e.authorizationserver.service.core;

import com.t3e.authorizationserver.model.User;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * Manage {@link User}
 * */
public interface UserService extends UserDetailsManager {
}
