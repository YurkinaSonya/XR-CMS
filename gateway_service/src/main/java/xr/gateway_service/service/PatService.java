package xr.gateway_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xr.gateway_service.data.dto.extra.PatInfo;
import xr.gateway_service.data.dto.request.PatCreateRequest;
import xr.gateway_service.data.dto.response.PatCreateResponse;
import xr.gateway_service.data.entity.User;
import xr.gateway_service.data.entity.UserPat;
import xr.gateway_service.data.repository.UserPatRepository;
import xr.gateway_service.data.repository.UserRepository;

import java.nio.file.AccessDeniedException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PatService {

    private final UserPatRepository patRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Transactional
    public PatCreateResponse createForUser(UUID userId, PatCreateRequest req, int rawLength) {
        User user = userRepository.findById(userId).orElseThrow();
        String raw = generateSecret(rawLength);
        UserPat pat = new UserPat();
        pat.setId(UUID.randomUUID());
        pat.setUser(user);
        pat.setName(req.name());
        pat.setScopes(req.scopes());
        pat.setSecretHash(encoder.encode(raw));
        pat.setCreatedAt(Instant.now());
        pat.setExpiresAt(req.expiresAt());
        patRepository.save(pat);
        return new PatCreateResponse(pat.getId(), raw, pat.getExpiresAt());
    }

    public List<PatInfo> listForUser(UUID userId) {
        return StreamSupport.stream(patRepository.findAll().spliterator(), false)
                .filter(p -> p.getUser().getId().equals(userId))
                .map(p -> new PatInfo(p.getId(), p.getName(), p.getScopes(), p.getCreatedAt(), p.getExpiresAt(), p.getRevokedAt()))
                .toList();

    }

    @Transactional
    public void revoke(UUID userId, UUID patId) throws AccessDeniedException {
        UserPat pat = patRepository.findById(patId).orElseThrow();
        if (!pat.getUser().getId().equals(userId)) throw new AccessDeniedException("Not your token");
        pat.setRevokedAt(Instant.now());
    }

    private String generateSecret(int length) {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        return sb.toString();
    }
}
