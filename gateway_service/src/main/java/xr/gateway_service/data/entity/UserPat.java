package xr.gateway_service.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="user_pat")
@Getter
@Setter
public class UserPat {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="user_id") private User user;
    private String name;
    private String secretHash;
    private String scopes;
    private Instant createdAt;
    private Instant expiresAt;
    private Instant revokedAt;

    public boolean isActiveNow() {
        return revokedAt == null && (expiresAt == null || expiresAt.isAfter(Instant.now()));
    }
}
