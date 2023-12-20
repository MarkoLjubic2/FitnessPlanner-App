package org.raf.sk.appointmentservice.mapper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.raf.sk.appointmentservice.domain.Review;
import org.raf.sk.appointmentservice.dto.CreateReviewDto;
import org.raf.sk.appointmentservice.dto.ReviewDto;
import org.raf.sk.appointmentservice.repository.ReservationRepository;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReviewMapper {

    private final ReservationRepository reservationRepository;

    public Review reviewDtoToReview(CreateReviewDto createReviewDto) {
        Review review = new Review();
        review.setReservation(reservationRepository.findById(createReviewDto.getReservationId()).get());
        review.setComment(createReviewDto.getComment());
        review.setMark(createReviewDto.getMark());
        return review;
    }

    public ReviewDto reviewToReviewDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setReservationId(review.getReservation().getId());
        reviewDto.setComment(review.getComment());
        reviewDto.setMark(review.getMark());
        return reviewDto;
    }

    public Review createReviewDtoToReview(CreateReviewDto createReviewDto) {
        Review review = new Review();
        review.setReservation(reservationRepository.findById(createReviewDto.getReservationId()).get());
        review.setComment(createReviewDto.getComment());
        review.setMark(createReviewDto.getMark());
        return review;
    }

    public CreateReviewDto reviewToCreateReviewDto(Review review) {
        CreateReviewDto createReviewDto = new CreateReviewDto();
        createReviewDto.setReservationId(review.getReservation().getId());
        createReviewDto.setComment(review.getComment());
        createReviewDto.setMark(review.getMark());
        return createReviewDto;
    }


}
