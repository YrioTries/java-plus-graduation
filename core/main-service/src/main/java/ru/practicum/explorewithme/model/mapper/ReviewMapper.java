package ru.practicum.explorewithme.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.model.dto.review.NewReviewDto;
import ru.practicum.explorewithme.model.dto.review.ReviewDto;
import ru.practicum.explorewithme.model.dao.ReviewDao;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDto toReviewDto(ReviewDao review);

    ReviewDao toReview(NewReviewDto dto);

}
