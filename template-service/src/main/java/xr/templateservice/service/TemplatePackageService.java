package xr.templateservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import xr.templateservice.data.dto.model.ValidationIssueDto;
import xr.templateservice.data.dto.model.assets.AssetRefDto;
import xr.templateservice.data.dto.model.assets.EnvelopeAssetDto;
import xr.templateservice.data.dto.model.blueprints.EnvelopeDto;
import xr.templateservice.data.dto.model.blueprints.ManifestDto;
import xr.templateservice.data.dto.model.entities.TemplateIndexProjection;
import xr.templateservice.data.dto.request.CreateTemplatePackageRequest;
import xr.templateservice.data.dto.response.CreateTemplatePackageResponse;
import xr.templateservice.data.entity.TemplateIndexEntity;
import xr.templateservice.data.entity.TemplatePackageEntity;
import xr.templateservice.data.repository.TemplateIndexRepository;
import xr.templateservice.data.repository.TemplatePackageRepository;
import xr.templateservice.exception.TemplateValidationException;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplatePackageService {

    private static final Pattern SEMVER_PATTERN =
            Pattern.compile("^\\d+\\.\\d+\\.\\d+(-[0-9A-Za-z.-]+)?$");

    private final TemplatePackageRepository packageRepository;
    private final TemplateIndexRepository indexRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public CreateTemplatePackageResponse createPackage(CreateTemplatePackageRequest request) {

        EnvelopeDto envelope = request.getEnvelope();
        ManifestDto manifest = request.getManifest();
        List<AssetRefDto> assetRefs = Optional.ofNullable(request.getAssets())
                .orElseGet(List::of);

        validateEnvelope(envelope);

        List<ValidationIssueDto> warnings = new ArrayList<>();
        validateManifest(manifest, envelope, warnings);

        List<AssetRefDto> normalizedAssets = reconcileAssets(envelope, assetRefs);

        TemplatePackageEntity.PackageStatus status =
                warnings.isEmpty()
                        ? TemplatePackageEntity.PackageStatus.PUBLISHED
                        : TemplatePackageEntity.PackageStatus.DRAFT;

        packageRepository.findByBlueprintIdAndVersion(envelope.getBlueprintId(), envelope.getVersion())
                .ifPresent(existing -> {
                    throw new TemplateValidationException(
                            "Blueprint version already exists",
                            List.of(new ValidationIssueDto(
                                    "VERSION_EXISTS",
                                    "envelope.version",
                                    "Blueprint " + envelope.getBlueprintId() +
                                            " version " + envelope.getVersion() + " already exists"
                            ))
                    );
                });

        TemplatePackageEntity entity = new TemplatePackageEntity();
        entity.setPackageId(generatePackageId());
        entity.setBlueprintId(envelope.getBlueprintId());
        entity.setVersion(envelope.getVersion());
        entity.setStatus(status);
        entity.setIngestId(request.getIngestId());

        try {
            entity.setEnvelopeJson(objectMapper.writeValueAsString(envelope));
            entity.setManifestJson(objectMapper.writeValueAsString(manifest));
            entity.setAssetsJson(objectMapper.writeValueAsString(normalizedAssets));
            if (!warnings.isEmpty()) {
                entity.setWarningsJson(objectMapper.writeValueAsString(warnings));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize JSON payload", e);
        }

        packageRepository.save(entity);
        updateBlueprintIndex(entity, envelope);

        TemplateIndexProjection indexProjection = new TemplateIndexProjection(
                envelope.getIndex().getParamPaths(),
                envelope.getIndex().getRepeatables()
        );

        return new CreateTemplatePackageResponse(
                entity.getPackageId(),
                entity.getBlueprintId(),
                entity.getVersion(),
                status == TemplatePackageEntity.PackageStatus.PUBLISHED ? "published" : "draft",
                warnings,
                indexProjection
        );
    }

    private void validateEnvelope(EnvelopeDto envelope) {
        if (envelope == null) {
            throw new TemplateValidationException("Envelope is required");
        }
        if (envelope.getBlueprintId() == null || envelope.getBlueprintId().isBlank()) {
            throw new TemplateValidationException("Envelope.blueprintId is required");
        }
        if (envelope.getVersion() == null || !SEMVER_PATTERN.matcher(envelope.getVersion()).matches()) {
            throw new TemplateValidationException("Envelope.version must be valid SemVer");
        }
        if (envelope.getIndex() == null
                || envelope.getIndex().getParamPaths() == null
                || envelope.getIndex().getRepeatables() == null) {
            throw new TemplateValidationException("Envelope.index.paramPaths/repeatables are required");
        }
        if (envelope.getAssets() == null) {
            throw new TemplateValidationException("Envelope.assets is required (can be empty array)");
        }
        // TODO: validation
    }

    private void validateManifest(ManifestDto manifest,
                                  EnvelopeDto envelope,
                                  List<ValidationIssueDto> warningsOut) {

        if (manifest == null) {
            throw new TemplateValidationException("Manifest is required");
        }

        // Кросс-проверка id/version между envelope и manifest
        if (!Objects.equals(envelope.getBlueprintId(), manifest.getId())) {
            throw new TemplateValidationException(
                    "Manifest.id must match envelope.blueprintId");
        }
        if (!Objects.equals(envelope.getVersion(), manifest.getVersion())) {
            throw new TemplateValidationException(
                    "Manifest.version must match envelope.version");
        }

        //TODO schema validation
        if (manifest.getSchema() == null || manifest.getSchema().isBlank()) {
            warningsOut.add(new ValidationIssueDto(
                    "SCHEMA_MISSING",
                    "$schema",
                    "Manifest.$schema is not specified; schema validation skipped"
            ));
            return;
        }
    }

    private List<AssetRefDto> reconcileAssets(EnvelopeDto envelope, List<AssetRefDto> assetRefs) {
        Map<String, AssetRefDto> byId = assetRefs.stream()
                .collect(Collectors.toMap(AssetRefDto::getId, a -> a, (a, b) -> a));

        List<ValidationIssueDto> issues = new ArrayList<>();

        List<AssetRefDto> result = new ArrayList<>();
        for (EnvelopeAssetDto envAsset : envelope.getAssets()) {
            AssetRefDto ref = byId.get(envAsset.getId());
            if (ref == null) {
                issues.add(new ValidationIssueDto(
                        "MISSING_ASSET_REF",
                        "assets[id=" + envAsset.getId() + "]",
                        "No AssetRef provided for envelope asset id=" + envAsset.getId()
                ));
                continue;
            }
            if (ref.getType() != null && !ref.getType().equals(envAsset.getType())) {
                issues.add(new ValidationIssueDto(
                        "ASSET_TYPE_MISMATCH",
                        "assets[id=" + envAsset.getId() + "]",
                        "Type mismatch between envelope (" + envAsset.getType() +
                                ") and AssetRef (" + ref.getType() + ")"
                ));
            }
            result.add(ref);
        }

        if (!issues.isEmpty()) {
            throw new TemplateValidationException("Asset reconciliation failed", issues);
        }

        return result;
    }

    private void updateBlueprintIndex(TemplatePackageEntity pkg, EnvelopeDto envelope) {
        indexRepository.clearLatestForBlueprint(pkg.getBlueprintId());

        TemplateIndexEntity index = new TemplateIndexEntity();
        index.setBlueprintId(pkg.getBlueprintId());
        index.setVersion(pkg.getVersion());
        index.setPackageId(pkg.getPackageId());
        index.setStatus(pkg.getStatus());
        index.setLatest(true);

        try {
            index.setTargetsJson(objectMapper.writeValueAsString(envelope.getTargets()));
            index.setCapabilitiesJson(objectMapper.writeValueAsString(envelope.getCapabilities()));
            index.setParamPathsJson(objectMapper.writeValueAsString(envelope.getIndex().getParamPaths()));
            index.setRepeatablesJson(objectMapper.writeValueAsString(envelope.getIndex().getRepeatables()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize index JSON", e);
        }

        indexRepository.save(index);
    }

    private String generatePackageId() {
        return "bp_pkg_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}