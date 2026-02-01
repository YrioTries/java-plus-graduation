package ru.practicum.explore_with_me.interaction_api.model.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewDto {
    @NotBlank
    @Size(min = 2, max = 2000)
    private String text;
}
