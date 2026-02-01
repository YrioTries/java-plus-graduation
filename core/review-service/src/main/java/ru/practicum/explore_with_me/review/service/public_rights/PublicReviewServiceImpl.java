package ru.practicum.explore_with_me.review.service.public_rights;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.interaction_api.exception.NotFoundException;
import ru.practicum.explore_with_me.interaction_api.model.event.client.EventServiceClient;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.ReviewDto;
import ru.practicum.explore_with_me.review.dao.Review;
import ru.practicum.explore_with_me.review.mapper.ReviewMapper;
import ru.practicum.explore_with_me.review.repository.ReviewRepository;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicReviewServiceImpl implements PublicReviewService {
    private final ReviewRepository reviewRepository;
    private final EventServiceClient eventServiceClient;

    private final ReviewMapper reviewMapper;

    private static final String serviceName = "[REVIEW-SERVICE]";

    @Override
    public List<ReviewDto> getEventReviewsByPublic(Long eventId, Integer from, Integer size) {
        log.debug("Запрос на получение event клиентом из getEventReviewsByPublic сервиса {}", serviceName);
        eventServiceClient.validateEventExistingById(eventId);

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Review> reviews = reviewRepository.findAllByEventId(eventId, pageRequest);
        return reviews
                .stream()
                .map(reviewMapper::toReviewDto)
                .toList();
    }
}
