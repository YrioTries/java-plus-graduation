package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.model.dao.CategoryDao;

public interface CategoryRepository extends JpaRepository<CategoryDao, Long> {
    Boolean existsByName(String name);
}