package xr.templateservice.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "blueprint_index")
@Getter
@Setter
public class TemplateIndexEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blueprint_id", nullable = false, length = 255)
    private String blueprintId;

    @Column(name = "version", nullable = false, length = 32)
    private String version;

    @Column(name = "package_id", nullable = false, length = 64)
    private String packageId;

    @Column(name = "targets_json", columnDefinition = "json", nullable = false)
    private String targetsJson;

    @Column(name = "capabilities_json", columnDefinition = "json", nullable = false)
    private String capabilitiesJson;

    @Column(name = "param_paths_json", columnDefinition = "json", nullable = false)
    private String paramPathsJson;

    @Column(name = "repeatables_json", columnDefinition = "json", nullable = false)
    private String repeatablesJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private TemplatePackageEntity.PackageStatus status;

    @Column(name = "is_latest", nullable = false)
    private boolean latest;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;
}
