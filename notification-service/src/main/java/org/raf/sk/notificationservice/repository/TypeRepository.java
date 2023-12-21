package org.raf.sk.notificationservice.repository;

import org.raf.sk.notificationservice.domain.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {

    Optional<Type> getTypeById(Long id);

    Optional<Type> findTypeByName(String name);

}
