package ru.practicum.explore_with_me.review.service.public_rights;

import ru.practicum.explore_with_me.interaction_api.model.review.dto.ReviewDto;

import java.util.List;

public interface PublicReviewService {

    List<ReviewDto> getEventReviewsByPublic(Long eventId, Integer from, Integer size);

}
