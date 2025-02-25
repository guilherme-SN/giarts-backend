package com.giarts.ateliegiarts.security;

import com.giarts.ateliegiarts.enums.EUserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    public boolean canAccessUser(Long expectedUserId) {
        UserDetailsImpl userDetails = getAuthenticatedUser();

        boolean isAdmin = isUserAdmin(userDetails);
        boolean isOwner = isUserOwner(userDetails, expectedUserId);

        return (isAdmin || isOwner);
    }

    private UserDetailsImpl getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthenticationValid(authentication)) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }

        throw new IllegalStateException("User not authenticated");
    }

    private boolean isAuthenticationValid(Authentication authentication) {
        return authentication != null && (authentication.getPrincipal() instanceof UserDetailsImpl);
    }

    private boolean isUserAdmin(UserDetailsImpl userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals(EUserRole.ROLE_ADMIN.name())
                );
    }

    private boolean isUserOwner(UserDetailsImpl userDetails, Long expectedUserId) {
        return userDetails.getUser().getId().equals(expectedUserId);
    }
}
