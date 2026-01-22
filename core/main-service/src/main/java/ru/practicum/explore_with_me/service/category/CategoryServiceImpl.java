package ru.practicum.explore_with_me.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.model.dto.category.CategoryDto;
import ru.practicum.explore_with_me.model.dto.category.NewCategoryDto;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.model.mapper.CategoryMapper;
import ru.practicum.explore_with_me.model.dao.Category;
import ru.practicum.explore_with_me.repository.CategoryRepository;
import ru.practicum.explore_with_me.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements ru.practicum.explore_with_me.service.category.CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Category name must be unique");
        }

        Category category = categoryMapper.toCategoryFromNew(newCategoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (!eventRepository.findByCategoryId(catId).isEmpty()) {
            throw new ConflictException("Cannot delete category with associated events");
        }

        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (!category.getName().equals(categoryDto.getName()) &&
                categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Category name must be unique");
        }

        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toCategoryDto)
                .getContent();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return categoryMapper.toCategoryDto(category);
    }
}