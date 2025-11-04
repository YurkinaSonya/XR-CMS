package xr.gateway_service.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import xr.gateway_service.data.entity.User;

import java.util.Collection;

public class PatAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;
    private final User principal;
    public PatAuthenticationToken(String token) { super(null); this.token = token; this.principal = null; setAuthenticated(false); }
    public PatAuthenticationToken(User principal, Collection<? extends GrantedAuthority> auths, String token) {
        super(auths); this.principal = principal; this.token = token; setAuthenticated(true);
    }
    @Override public Object getCredentials() { return token; }
    @Override public Object getPrincipal() { return principal; }
}