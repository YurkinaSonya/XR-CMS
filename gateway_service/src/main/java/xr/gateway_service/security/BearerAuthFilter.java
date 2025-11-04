package xr.gateway_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BearerAuthFilter extends OncePerRequestFilter {

    private final AuthenticationManager authManager;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String token = h.substring(7);

            Authentication auth = null;
            try {
                if (token.chars().filter(ch -> ch == '.').count() == 2) {
                    // TODO: JwtAuthenticationProvider
                    throw new BadCredentialsException("JWT not supported yet");
                } else {
                    auth = authManager.authenticate(new PatAuthenticationToken(token));
                }
            } catch (AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                throw ex;
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(req, res);
    }
}