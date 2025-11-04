package xr.gateway_service.data.dto.extra;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

public record PatInfo(
        UUID id,
        String name,
        String scopes,
        Instant createdAt,
        Instant expiresAt,
        Instant revokedAt
) {}
