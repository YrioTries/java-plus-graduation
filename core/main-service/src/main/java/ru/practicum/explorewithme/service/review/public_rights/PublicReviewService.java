package ru.practicum.explorewithme.service.review.public_rights;

import ru.practicum.explorewithme.model.dto.review.ReviewDto;

import java.util.List;

public interface PublicReviewService {

    List<ReviewDto> getEventReviewsByPublic(Long eventId, Integer from, Integer size);

}
