package ru.practicum.explore_with_me.model.dao;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.explore_with_me.model.enums.RequestStatus;

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
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    Long id;
    @Builder.Default
    LocalDateTime created = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;
    @Enumerated(value = EnumType.STRING)
    RequestStatus status;

}