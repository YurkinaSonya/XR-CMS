package xr.gateway_service.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="user")
@Getter
@Setter
public class User {
    @Id
    private UUID id;
    private String email;
    private String displayName;
    private boolean isActive = true;
    private Instant createdAt;
    private Instant updatedAt;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_roles",
            joinColumns=@JoinColumn(name="user_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="role_id", referencedColumnName="id"))
    private Set<Role> roles = new HashSet<>();
}