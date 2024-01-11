package org.raf.sk.appointmentservice.mapper;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.Review;
import org.raf.sk.appointmentservice.dto.review.CreateReviewDto;
import org.raf.sk.appointmentservice.dto.review.ReviewDto;
import org.raf.sk.appointmentservice.repository.ReservationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ReviewMapper {

    private final ReservationRepository reservationRepository;

    public Review reviewDtoToReview(ReviewDto reviewDto) {
        return Optional.of(reviewDto)
                .map(dto -> {
                    Review review = new Review();
                    reservationRepository.findById(dto.getReservationId()).ifPresent(review::setReservation);
                    review.setComment(dto.getComment());
                    review.setMark(dto.getMark());
                    return review;
                })
                .orElse(null);
    }

    public ReviewDto reviewToReviewDto(Review review) {
        return Optional.ofNullable(review)
                .map(r -> {
                    ReviewDto reviewDto = new ReviewDto();
                    reviewDto.setReservationId(r.getReservation().getId());
                    reviewDto.setComment(r.getComment());
                    reviewDto.setMark(r.getMark());
                    return reviewDto;
                })
                .orElse(null);
    }

    public Review createReviewDtoToReview(CreateReviewDto createReviewDto) {
        return Optional.of(createReviewDto)
                .map(dto -> {
                    Review review = new Review();
                    reservationRepository.findById(dto.getReservationId()).ifPresent(review::setReservation);
                    review.setComment(dto.getComment());
                    review.setMark(dto.getMark());
                    return review;
                })
                .orElse(null);
    }

    public CreateReviewDto reviewToCreateReviewDto(Review review) {
        return Optional.ofNullable(review)
                .map(r -> {
                    CreateReviewDto createReviewDto = new CreateReviewDto();
                    createReviewDto.setReservationId(r.getReservation().getId());
                    createReviewDto.setComment(r.getComment());
                    createReviewDto.setMark(r.getMark());
                    return createReviewDto;
                })
                .orElse(null);
    }

}
