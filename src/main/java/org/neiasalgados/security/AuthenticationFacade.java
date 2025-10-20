package org.neiasalgados.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {

    public UserSecurity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        return (UserSecurity) authentication.getPrincipal();
    }

    public Long getAuthenticatedUserId() {
        UserSecurity userSecurity = getAuthenticatedUser();
        return userSecurity != null ? userSecurity.getId() : null;
    }
}