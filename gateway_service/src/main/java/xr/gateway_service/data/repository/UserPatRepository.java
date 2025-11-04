package xr.gateway_service.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xr.gateway_service.data.entity.UserPat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPatRepository extends JpaRepository<UserPat, UUID> {
    @Query("select p from UserPat p join fetch p.user u join fetch u.roles r where p.revokedAt is null")
    List<UserPat> findAllActive();

    @Query("select p from UserPat p join fetch p.user u join fetch u.roles r where p.revokedAt is null and p.expiresAt is null or p.expiresAt > CURRENT_TIMESTAMP")
    List<UserPat> findAllNonExpired();

    Optional<UserPat> findById(UUID id);
}