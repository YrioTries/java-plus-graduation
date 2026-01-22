package ru.practicum.explore_with_me.review.collector.private_rights;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.NewReviewDto;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.ReviewDto;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.UpdateReviewDto;
import ru.practicum.explore_with_me.review.service.private_rights.PrivateReviewService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/reviews")
public class PrivateReviewController {
    private final PrivateReviewService reviewService;

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto addReview(@Positive @PathVariable Long userId,
                               @Positive @PathVariable Long eventId,
                               @RequestBody @Valid NewReviewDto dto) {
        log.info("POST-запрос на добавление отзыва к ивенту с id={}", eventId);
        return reviewService.addReview(userId, eventId, dto);
    }

    @PatchMapping("/{reviewId}")
    public ReviewDto updateReview(@Positive @PathVariable Long userId,
                                  @Positive @PathVariable Long reviewId,
                                  @RequestBody @Valid UpdateReviewDto dto) {
        log.info("PATCH-запрос на обновление отзыва с id={}", reviewId);
        return reviewService.updateReview(userId, reviewId, dto);
    }

    @DeleteMapping("/{reviewId}/events/{eventId}")
    public void deleteReviewByAuthor(@Positive @PathVariable Long userId,
                                     @Positive @PathVariable Long reviewId) {
        log.info("DELETE-запрос на удаление автором отзыва с id={}", reviewId);
        reviewService.deleteReviewByAuthor(userId, reviewId);
    }

    @GetMapping("/{reviewId}")
    public ReviewDto getReviewById(@Positive @PathVariable Long userId,
                                   @Positive @PathVariable Long reviewId) {
        log.info("GET-запрос на просмотр автором отзыва с id={}", reviewId);
        return reviewService.getReviewById(userId, reviewId);
    }

    @GetMapping
    public List<ReviewDto> getReviewsByAuthor(@Positive @PathVariable Long userId) {
        log.info("GET-запрос на просмотр всех отзывов автором с id={}", userId);
        return reviewService.getReviewsByAuthor(userId);
    }
}
