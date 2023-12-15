package org.raf.sk.userservice.repository;

import org.raf.sk.userservice.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {

    Optional<Status> findStatusByName(String name);

    Optional<Status> getStatusById(Long id);
}