package com.example.googledriveclone.repository;

import com.example.googledriveclone.models.ERole;
import com.example.googledriveclone.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
