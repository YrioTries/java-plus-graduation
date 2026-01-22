package ru.practicum.explore_with_me.review.service.admin_rights;

import ru.practicum.explore_with_me.model.dto.review.ReviewDto;

import java.util.List;

public interface AdminReviewService {

    List<ReviewDto> getReviewsByAdmin(String text,
                                      List<Long> users,
                                      List<Long> events,
                                      Integer from,
                                      Integer size);

    void deleteReview(Long reviewId);

}
