package com.project.ensureQuality.repository;

import com.project.ensureQuality.model.ERole;
import com.project.ensureQuality.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
