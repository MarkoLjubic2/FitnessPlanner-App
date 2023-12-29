package org.raf.sk.appointmentservice.repository;

import org.raf.sk.appointmentservice.domain.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {

    Optional<Hall> getHallById(Long id);

    Optional<Hall> findHallByName(String name);

    @Modifying
    @Query("update Hall h set h.name = :name, h.description = :description, h.coaches = :coaches, h.managerId = :managerId where h.id = :id")
    void updateHallById(Long id, String name, String description, int coaches, Long managerId);

    @Modifying
    @Query("delete from Hall h where h.id = :id")
    void deleteHallById(Long id);

}
