package xr.templateservice.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xr.templateservice.data.entity.TemplateIndexEntity;

public interface TemplateIndexRepository extends JpaRepository<TemplateIndexEntity, Long> {

    @Modifying
    @Query("update TemplateIndexEntity b set b.latest = false where b.blueprintId = :blueprintId")
    void clearLatestForBlueprint(String blueprintId);
}