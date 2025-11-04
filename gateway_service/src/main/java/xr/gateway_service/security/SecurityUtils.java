package xr.gateway_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import xr.gateway_service.data.entity.User;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public final class SecurityUtils {
    private SecurityUtils() {}
    public static UUID currentUserId() throws AccessDeniedException {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getPrincipal() == null) throw new AccessDeniedException("No auth");
        if (a.getPrincipal() instanceof User u) return u.getId();
        throw new AccessDeniedException("Invalid principal");
    }
    public static boolean hasRole(String role) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.getAuthorities().stream().anyMatch(gr -> gr.getAuthority().equals("ROLE_" + role));
    }
}
