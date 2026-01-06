package com.br.deliveryapi.security;


import com.br.deliveryapi.entity.User;
import com.br.deliveryapi.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    private AuthUserDetails(Long id, String email, String password, Role role, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.authorities = authorities;
    }

    public static AuthUserDetails fromUser(User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        return new AuthUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                List.of(authority)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // Username será o email
    @Override
    public String getUsername() {
        return email;
    }

}
