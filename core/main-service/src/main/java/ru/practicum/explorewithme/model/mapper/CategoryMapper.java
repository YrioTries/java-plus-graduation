package ru.practicum.explorewithme.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.model.dto.category.CategoryDto;
import ru.practicum.explorewithme.model.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.model.dao.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategory(CategoryDto categoryDto);

    Category toCategoryFromNew(NewCategoryDto newCategoryDto);
}