package ru.practicum.explore_with_me.service.review.public_rights;

import ru.practicum.explore_with_me.model.dto.review.ReviewDto;

import java.util.List;

public interface PublicReviewService {

    List<ReviewDto> getEventReviewsByPublic(Long eventId, Integer from, Integer size);

}
