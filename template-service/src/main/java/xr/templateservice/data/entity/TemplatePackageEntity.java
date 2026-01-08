package xr.templateservice.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "blueprint_package")
@Getter
@Setter
public class TemplatePackageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_id", nullable = false, unique = true, length = 64)
    private String packageId;

    @Column(name = "blueprint_id", nullable = false, length = 255)
    private String blueprintId;

    @Column(name = "version", nullable = false, length = 32)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private PackageStatus status;

    @Column(name = "ingest_id", nullable = false, length = 64)
    private String ingestId;

    @Column(name = "envelope_json", columnDefinition = "json", nullable = false)
    private String envelopeJson;

    @Column(name = "manifest_json", columnDefinition = "json", nullable = false)
    private String manifestJson;

    @Column(name = "assets_json", columnDefinition = "json", nullable = false)
    private String assetsJson;

    @Column(name = "warnings_json", columnDefinition = "json")
    private String warningsJson;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    public enum PackageStatus {
        DRAFT,
        PUBLISHED
    }
}