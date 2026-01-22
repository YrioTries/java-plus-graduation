package ru.practicum.explore_with_me.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore_with_me.model.dto.review.NewReviewDto;
import ru.practicum.explore_with_me.model.dto.review.ReviewDto;
import ru.practicum.explore_with_me.model.dao.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDto toReviewDto(Review review);

    Review toReview(NewReviewDto dto);

}
