package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.explorewithme.model.dao.ReviewDao;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<ReviewDao, Long>, JpaSpecificationExecutor<ReviewDao> {

    List<ReviewDao> findAllByEventId(Long eventId, Pageable pageable);

    Optional<ReviewDao> findByIdAndAuthorId(Long id, Long authorId);

    List<ReviewDao> findAllByAuthorId(Long authorId);

    Optional<ReviewDao> findByEventIdAndAuthorId(Long eventId, Long authorId);

}