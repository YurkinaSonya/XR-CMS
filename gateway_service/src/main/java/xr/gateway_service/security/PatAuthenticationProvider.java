package xr.gateway_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import xr.gateway_service.data.entity.User;
import xr.gateway_service.data.entity.UserPat;
import xr.gateway_service.data.repository.UserPatRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PatAuthenticationProvider implements AuthenticationProvider {

    private final UserPatRepository patRepo;
    private final PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String presented = (String) authentication.getCredentials();
        // TODO: prefix
        for (UserPat pat : patRepo.findAllNonExpired()) {
            if (pat.isActiveNow() && encoder.matches(presented, pat.getSecretHash())) {
                User u = pat.getUser();
                if (!u.isActive()) throw new BadCredentialsException("User disabled");
                List<SimpleGrantedAuthority> auths = u.getRoles().stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                        .toList();
                return new PatAuthenticationToken(u, auths, "PAT");
            }
        }
        throw new BadCredentialsException("Invalid PAT");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PatAuthenticationToken.class.isAssignableFrom(authentication);
    }
}