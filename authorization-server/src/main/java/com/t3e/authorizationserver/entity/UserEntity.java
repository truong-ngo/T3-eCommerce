package com.t3e.authorizationserver.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "user")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Column(name = "account_non_expired")
    private boolean accountNonExpired;
    @Column(name = "account_non_locked")
    private boolean accountNonLocked;
    @Column(name = "credential_non_expired")
    private boolean credentialsNonExpired;
    private boolean enabled;
    private String roles;
}
