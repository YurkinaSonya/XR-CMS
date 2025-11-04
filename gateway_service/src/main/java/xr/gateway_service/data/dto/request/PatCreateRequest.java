package xr.gateway_service.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public record PatCreateRequest(
        @NotBlank String name,
        @Size(min = 0, max = 512) String scopes,
        Instant expiresAt
) {}