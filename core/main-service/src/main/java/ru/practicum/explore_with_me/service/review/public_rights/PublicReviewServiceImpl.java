package ru.practicum.explore_with_me.service.review.public_rights;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.model.dto.review.ReviewDto;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.model.mapper.ReviewMapper;
import ru.practicum.explore_with_me.model.dao.Review;
import ru.practicum.explore_with_me.repository.EventRepository;
import ru.practicum.explore_with_me.repository.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicReviewServiceImpl implements ru.practicum.explore_with_me.service.review.public_rights.PublicReviewService {
    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;

    private final ReviewMapper reviewMapper;

    @Override
    public List<ReviewDto> getEventReviewsByPublic(Long eventId, Integer from, Integer size) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Ивента с id=" + eventId + " нет в БД!");
        }
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Review> reviews = reviewRepository.findAllByEventId(eventId, pageRequest);
        return reviews
                .stream()
                .map(reviewMapper::toReviewDto)
                .toList();
    }
}
