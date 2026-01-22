package ru.practicum.explore_with_me.category.controller.admin_rights;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.model.dto.category.CategoryDto;
import ru.practicum.explore_with_me.model.dto.category.NewCategoryDto;
import ru.practicum.explore_with_me.service.category.CategoryService;

import jakarta.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return categoryService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Positive Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable @Positive Long catId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.updateCategory(catId, categoryDto);
    }

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return categoryService.getCategories(PageRequest.of(from / size, size));
    }
}
