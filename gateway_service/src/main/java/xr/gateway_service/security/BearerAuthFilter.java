package xr.gateway_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BearerAuthFilter extends OncePerRequestFilter {

    private final AuthenticationManager authManager;

    public BearerAuthFilter(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String token = h.substring(7);

            try {
                Authentication auth;
                if (token.chars().filter(ch -> ch == '.').count() == 2) {
                    // TODO: JwtAuthenticationProvider
                    throw new BadCredentialsException("JWT not supported yet");
                } else {
                    auth = authManager.authenticate(new PatAuthenticationToken(token));
                }
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                throw ex;
            }
        }
        chain.doFilter(req, res);
    }
}
