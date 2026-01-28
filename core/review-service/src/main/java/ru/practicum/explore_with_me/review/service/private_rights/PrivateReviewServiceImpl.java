package ru.practicum.explore_with_me.review.service.private_rights;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.interaction_api.exception.ConflictException;
import ru.practicum.explore_with_me.interaction_api.exception.NotFoundException;
import ru.practicum.explore_with_me.interaction_api.model.event.EventState;
import ru.practicum.explore_with_me.interaction_api.model.event.client.EventServiceClient;
import ru.practicum.explore_with_me.interaction_api.model.event.dto.EventFullDto;
import ru.practicum.explore_with_me.interaction_api.model.request.RequestStatus;
import ru.practicum.explore_with_me.interaction_api.model.request.client.ParticipationRequestServiceClient;
import ru.practicum.explore_with_me.interaction_api.model.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.NewReviewDto;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.ReviewDto;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.UpdateReviewDto;
import ru.practicum.explore_with_me.interaction_api.model.user.client.UserServiceClient;
import ru.practicum.explore_with_me.interaction_api.model.user.dto.UserShortDto;
import ru.practicum.explore_with_me.review.dao.Review;
import ru.practicum.explore_with_me.review.mapper.ReviewMapper;
import ru.practicum.explore_with_me.review.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateReviewServiceImpl implements PrivateReviewService {
    private final ReviewRepository reviewRepository;

    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;
    private final ParticipationRequestServiceClient requestServiceClient;

    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewDto addReview(Long userId, Long eventId, NewReviewDto dto) {

        UserShortDto userShortDto = userServiceClient.getUserShortDtoClientById(userId);

        log.debug("Запрос на получение event клиентом из addReview сервиса PrivateReviewServiceImpl");
        EventFullDto eventFullDto = eventServiceClient.getEventFullDtoByIdClient(eventId);

        if (reviewRepository.findByEventIdAndAuthorId(eventId, userId).isPresent()) {
            throw new ConflictException("Юзер с id=" + userId + " уже написал отзыв к ивенту с id=" + eventId + "!");
        }
        verifyReview(userShortDto, eventFullDto);
        Review review = reviewMapper.toReview(dto);
        review.setAuthorId(userShortDto.getId());
        review.setEventId(eventFullDto.getId());
        review.setCreatedOn(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewDto(savedReview);
    }

    @Override
    @Transactional
    public ReviewDto updateReview(Long userId, Long reviewId, UpdateReviewDto dto) {
        userServiceClient.validateUserExistingById(userId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыва с id=" + reviewId + " нет в БД!"));

        if (!review.getAuthorId().equals(userId)) {
            throw new ConflictException("Пользователь не является автором отзыва");
        }

        if (dto.getText() == null || dto.getText().isBlank() || dto.getText().equals(review.getText())) {
            return reviewMapper.toReviewDto(review);
        }
        review.setText(dto.getText());
        review.setLastUpdatedOn(LocalDateTime.now());
        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toReviewDto(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReviewByAuthor(Long userId, Long reviewId) {
        UserShortDto userShortDto = userServiceClient.getUserShortDtoClientById(userId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыва с id=" + reviewId + " нет в БД!"));

        if (!userShortDto.getId().equals(review.getAuthorId())) {
            throw new ConflictException("Пользователь не является автором отзыва");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public ReviewDto getReviewById(Long userId, Long reviewId) {
        userServiceClient.validateUserExistingById(userId);

        Review review = reviewRepository.findByIdAndAuthorId(reviewId, userId)
                .orElseThrow(() -> new NotFoundException("Юзер с id=" + reviewId + " не писал отзыв с id=" + reviewId + "!"));
        return reviewMapper.toReviewDto(review);
    }

    @Override
    public List<ReviewDto> getReviewsByAuthor(Long userId) {
        userServiceClient.validateUserExistingById(userId);

        return reviewRepository.findAllByAuthorId(userId)
                .stream()
                .map(reviewMapper::toReviewDto)
                .toList();
    }

    private void verifyReview(UserShortDto user, EventFullDto event) {
        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Инициатор ивента не может оставлять отзыв на свой ивент!");
        }
        if (!event.getState().equals(EventState.PUBLISHED.toString())) {
            throw new ConflictException("Чтобы оставить отзыв, статус ивента должен быть PUBLISHED!");
        }
        if (!event.getEventDate().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new ConflictException("Нельзя оставить отзыв на ивент, который ещё не закончился!");
        }
        ParticipationRequestDto requestDto = requestServiceClient.getUserRequestByUserIdAndEventId(user.getId(), event.getId());

        if (!requestDto.getStatus().equals(RequestStatus.CONFIRMED.toString())) {
            throw new ConflictException("Чтобы оставить отзыв, статус заявки юзера на участие в ивенте должен быть CONFIRMED!");
        }
    }
}
