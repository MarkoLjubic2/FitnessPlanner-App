package org.raf.sk.appointmentservice.repository;

import org.raf.sk.appointmentservice.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    Optional<Training> getTrainingById(Long id);

    Optional<Training> findTrainingByName(String name);

    @Modifying
    @Query("update Training t set t.name = :name, t.individual = :individual, t.price = :price where t.id = :id")
    void updateTrainingById(Long id, String name, boolean individual, double price);

    @Modifying
    @Query("delete from Training t where t.id = :id")
    void deleteTrainingById(Long id);

    @Query("select t from Training t where t.individual = :individual")
    Optional<Training> findTrainingByIndividual(boolean individual);

    @Query("select t from Training t where t.price = :price")
    Optional<Training> findTrainingByPrice(double price);

}
