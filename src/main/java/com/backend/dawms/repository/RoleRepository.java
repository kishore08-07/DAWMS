package com.backend.dawms.repository;

import com.backend.dawms.enums.RoleName;
import com.backend.dawms.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
//    Optional<Role> findByRole(RoleName name);

    Optional<Role> findByName(RoleName name);
}
