package ru.practicum.explorewithme.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.model.dto.category.CategoryDto;
import ru.practicum.explorewithme.model.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.model.dao.CategoryDao;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(CategoryDao category);

    CategoryDao toCategory(CategoryDto categoryDto);

    CategoryDao toCategoryFromNew(NewCategoryDto newCategoryDto);
}