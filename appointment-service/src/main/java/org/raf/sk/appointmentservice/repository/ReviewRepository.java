package org.raf.sk.appointmentservice.repository;

import org.raf.sk.appointmentservice.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> getReviewById(Long id);

    Optional<Review> findReviewByReservationId(Long reservationId);

    Optional<Review> findReviewByMark(int mark);

    @Modifying
    @Query("update Review r set r.comment = :comment, r.mark = :mark where r.id = :id")
    void updateReviewById(Long id, String comment, int mark);

    @Modifying
    @Query("delete from Review r where r.id = :id")
    void deleteReviewById(Long id);

}
