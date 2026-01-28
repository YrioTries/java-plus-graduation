package ru.practicum.explore_with_me.review.service.private_rights;

import ru.practicum.explore_with_me.interaction_api.model.review.dto.NewReviewDto;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.ReviewDto;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.UpdateReviewDto;

import java.util.List;

public interface PrivateReviewService {

    ReviewDto addReview(Long userId, Long eventId, NewReviewDto dto);

    void deleteReviewByAuthor(Long userId, Long reviewId);

    ReviewDto getReviewById(Long userId, Long reviewId);

    List<ReviewDto> getReviewsByAuthor(Long userId);

    ReviewDto updateReview(Long userId, Long reviewId, UpdateReviewDto dto);

}
