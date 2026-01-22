package ru.practicum.explore_with_me.review.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.NewReviewDto;
import ru.practicum.explore_with_me.interaction_api.model.review.dto.ReviewDto;
import ru.practicum.explore_with_me.review.dao.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDto toReviewDto(Review review);

    Review toReview(NewReviewDto dto);

}
