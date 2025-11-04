package xr.gateway_service.data.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

public record PatCreateResponse(
        UUID id,
        String secret,
        Instant expiresAt
) {}