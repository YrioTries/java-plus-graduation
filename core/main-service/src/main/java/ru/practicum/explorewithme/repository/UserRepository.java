package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.model.dao.UserDao;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDao, Long> {
    Page<UserDao> findByIdIn(List<Long> ids, Pageable pageable);

    Boolean existsByEmail(String email);
}