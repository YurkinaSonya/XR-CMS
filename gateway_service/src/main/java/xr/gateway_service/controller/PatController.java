package xr.gateway_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import xr.gateway_service.data.dto.extra.PatInfo;
import xr.gateway_service.data.dto.request.PatCreateRequest;
import xr.gateway_service.data.dto.response.PatCreateResponse;
import xr.gateway_service.security.SecurityUtils;
import xr.gateway_service.service.PatService;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth/pat")
@RequiredArgsConstructor
public class PatController {

    @Value("${gateway.security.pat.length}")
    private int patLength;

    private final PatService patService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public ResponseEntity<PatCreateResponse> create(@Valid @RequestBody PatCreateRequest req) throws AccessDeniedException {
        UUID uid = SecurityUtils.currentUserId();
        PatCreateResponse resp = patService.createForUser(uid, req, patLength);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public List<PatInfo> listMine() throws AccessDeniedException {
        UUID uid = SecurityUtils.currentUserId();
        return patService.listForUser(uid);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public ResponseEntity<Void> revoke(@PathVariable UUID id) throws AccessDeniedException {
        UUID uid = SecurityUtils.currentUserId();
        patService.revoke(uid, id);
        return ResponseEntity.noContent().build();
    }
}