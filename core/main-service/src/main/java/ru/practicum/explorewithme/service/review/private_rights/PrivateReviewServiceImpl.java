package ru.practicum.explorewithme.service.review.private_rights;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.model.dto.review.NewReviewDto;
import ru.practicum.explorewithme.model.dto.review.ReviewDto;
import ru.practicum.explorewithme.model.dto.review.UpdateReviewDto;
import ru.practicum.explorewithme.model.enums.EventState;
import ru.practicum.explorewithme.model.enums.RequestStatus;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.model.mapper.ReviewMapper;
import ru.practicum.explorewithme.model.dao.EventDao;
import ru.practicum.explorewithme.model.dao.ParticipationRequestDao;
import ru.practicum.explorewithme.model.dao.ReviewDao;
import ru.practicum.explorewithme.model.dao.UserDao;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;
import ru.practicum.explorewithme.repository.ReviewRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateReviewServiceImpl implements PrivateReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;

    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewDto addReview(Long userId, Long eventId, NewReviewDto dto) {
        UserDao user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " нет в БД!"));
        EventDao event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивента с id=" + eventId + " нет в БД!"));
        if (reviewRepository.findByEventIdAndAuthorId(eventId, userId).isPresent()) {
            throw new ConflictException("Юзер с id=" + userId + " уже написал отзыв к ивенту с id=" + eventId + "!");
        }
        verifyReview(user, event);
        ReviewDao review = reviewMapper.toReview(dto);
        review.setAuthor(user);
        review.setEvent(event);
        review.setCreatedOn(LocalDateTime.now());
        ReviewDao savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewDto(savedReview);
    }

    @Override
    @Transactional
    public ReviewDto updateReview(Long userId, Long reviewId, UpdateReviewDto dto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с id=" + userId + " нет в БД!");
        }
        ReviewDao review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыва с id=" + reviewId + " нет в БД!"));
        if (!review.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Пользователь не является автором отзыва");
        }
        if (dto.getText() == null || dto.getText().isBlank() || dto.getText().equals(review.getText())) {
            return reviewMapper.toReviewDto(review);
        }
        review.setText(dto.getText());
        review.setLastUpdatedOn(LocalDateTime.now());
        ReviewDao updatedReview = reviewRepository.save(review);
        return reviewMapper.toReviewDto(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReviewByAuthor(Long userId, Long reviewId) {
        UserDao user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " нет в БД!"));
        ReviewDao review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыва с id=" + reviewId + " нет в БД!"));
        if (!user.getId().equals(review.getAuthor().getId())) {
            throw new ConflictException("Пользователь не является автором отзыва");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public ReviewDto getReviewById(Long userId, Long reviewId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с id=" + userId + " нет в БД!");
        }
        ReviewDao review = reviewRepository.findByIdAndAuthorId(reviewId, userId)
                .orElseThrow(() -> new NotFoundException("Юзер с id=" + reviewId + " не писал отзыв с id=" + reviewId + "!"));
        return reviewMapper.toReviewDto(review);
    }

    @Override
    public List<ReviewDto> getReviewsByAuthor(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя с id=" + userId + " нет в БД!");
        }
        return reviewRepository.findAllByAuthorId(userId)
                .stream()
                .map(reviewMapper::toReviewDto)
                .toList();
    }

    private void verifyReview(UserDao user, EventDao event) {
        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Инициатор ивента не может оставлять отзыв на свой ивент!");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Чтобы оставить отзыв, статус ивента должен быть PUBLISHED!");
        }
        if (!event.getEventDate().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new ConflictException("Нельзя оставить отзыв на ивент, который ещё не закончился!");
        }
        ParticipationRequestDao request = requestRepository.findByEventIdAndRequesterId(event.getId(), user.getId())
                .orElseThrow(() -> new NotFoundException("Заявки юзера с id=" + user.getId() + " на участие в ивенте " +
                        "с id=" + event + " нет в БД!"));
        if (!request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ConflictException("Чтобы оставить отзыв, статус заявки юзера на участие в ивенте должен быть CONFIRMED!");
        }
    }
}
