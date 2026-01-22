package ru.practicum.explore_with_me.review.collector.admin_rights;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.ReviewDto;
import ru.practicum.explore_with_me.review.service.admin_rights.AdminReviewService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reviews")
public class AdminReviewController {
    private final AdminReviewService reviewService;

    @GetMapping
    public List<ReviewDto> getReviewsByAdmin(@RequestParam(required = false) String text,
                                             @RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<Long> events,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("GET-запрос админ-контроллера на поиск отзывов по заданным параметрам");
        log.info("Параметры поиска: text={}, users={}, events={}", text, users, events);
        return reviewService.getReviewsByAdmin(text, users, events, from, size);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@Positive @PathVariable Long reviewId) {
        log.info("DELETE-запрос админ-контроллера на удаление отзыва с id={}", reviewId);
        reviewService.deleteReview(reviewId);
    }
}
