package ru.practicum.explorewithme.model.dao;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.explorewithme.model.enums.RequestStatus;

import java.time.LocalDateTime;

import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "participation_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    Long id;
    @Builder.Default
    LocalDateTime created = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "event_id")
    EventDao event;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    UserDao requester;
    @Enumerated(value = EnumType.STRING)
    RequestStatus status;

}