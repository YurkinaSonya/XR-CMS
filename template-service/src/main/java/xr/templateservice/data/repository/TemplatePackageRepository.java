package xr.templateservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xr.templateservice.data.entity.TemplatePackageEntity;

import java.util.Optional;

public interface TemplatePackageRepository extends JpaRepository<TemplatePackageEntity, Long> {

    Optional<TemplatePackageEntity> findByBlueprintIdAndVersion(String blueprintId, String version);
}