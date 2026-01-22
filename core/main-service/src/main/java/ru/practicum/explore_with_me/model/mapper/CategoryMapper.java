package ru.practicum.explore_with_me.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore_with_me.model.dto.category.CategoryDto;
import ru.practicum.explore_with_me.model.dto.category.NewCategoryDto;
import ru.practicum.explore_with_me.model.dao.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategory(CategoryDto categoryDto);

    Category toCategoryFromNew(NewCategoryDto newCategoryDto);
}