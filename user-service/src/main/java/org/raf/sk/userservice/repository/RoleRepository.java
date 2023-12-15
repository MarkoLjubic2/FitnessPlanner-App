package org.raf.sk.userservice.repository;

import org.raf.sk.userservice.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findRoleByName(String name);

    Optional<Role> getRoleById(Long id);

}