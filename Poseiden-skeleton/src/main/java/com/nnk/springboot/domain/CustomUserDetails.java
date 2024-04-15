package com.nnk.springboot.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    /**
     * User
     */
    private final User user;

    /**
     * Constructor
     * @param user User to set
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Get authorities
     * @return Collection<? extends GrantedAuthority>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
    }

    /**
     * Get password
     * @return String
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Get username
     * @return String
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Is account non expired
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Is account non locked
     * @return boolean
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Is credentials non expired
     * @return boolean
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Is enabled
     * @return boolean
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

}
