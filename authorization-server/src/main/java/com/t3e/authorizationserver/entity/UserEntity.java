package com.t3e.authorizationserver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Entity(name = "user")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email(message = "Must be email format")
    private String email;
    @Length(min = 6, message = "Password is at least 6 character")
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
