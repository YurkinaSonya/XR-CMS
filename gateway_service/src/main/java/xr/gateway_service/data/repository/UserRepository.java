package xr.gateway_service.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xr.gateway_service.data.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
