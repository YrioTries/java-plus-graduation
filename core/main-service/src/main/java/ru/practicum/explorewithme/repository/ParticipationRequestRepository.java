package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.model.enums.RequestStatus;
import ru.practicum.explorewithme.model.dao.EventDao;
import ru.practicum.explorewithme.model.dao.ParticipationRequestDao;
import ru.practicum.explorewithme.model.dao.UserDao;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequestDao, Long> {

    List<ParticipationRequestDao> findByRequesterId(Long requesterId);

    List<ParticipationRequestDao> findByEventId(Long eventId);

    Optional<ParticipationRequestDao> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    Optional<ParticipationRequestDao> findByEventAndRequester(EventDao event, UserDao requester);

    List<ParticipationRequestDao> findByIdIn(List<Long> requestIds);

    @Query("SELECT COUNT(pr) FROM ParticipationRequest pr WHERE pr.event.id = :eventId AND pr.status = 'CONFIRMED'")
    Long countConfirmedRequestsByEventId(@Param("eventId") Long eventId);

    List<ParticipationRequestDao> findByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequestDao> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);

    @Modifying
    @Query("UPDATE ParticipationRequest pr SET pr.status = :status WHERE pr.id IN :requestIds")
    void updateStatusForRequests(@Param("requestIds") List<Long> requestIds, @Param("status") RequestStatus status);

    @Modifying
    @Query("UPDATE ParticipationRequest pr SET pr.status = :status WHERE pr.event.id = :eventId AND pr.status = :currentStatus")
    void updateStatusForPendingRequests(@Param("eventId") Long eventId, @Param("currentStatus") RequestStatus currentStatus, @Param("status") RequestStatus status);
}