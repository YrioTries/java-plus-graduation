package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.model.dao.CompilationDao;

public interface CompilationRepository extends JpaRepository<CompilationDao, Long> {
    Page<CompilationDao> findByPinned(Boolean pinned, Pageable pageable);
}