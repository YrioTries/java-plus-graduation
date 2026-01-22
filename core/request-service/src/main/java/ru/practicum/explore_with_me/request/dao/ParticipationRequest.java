package ru.practicum.explore_with_me.request.dao;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explore_with_me.interaction_api.model.request.RequestStatus;

import java.time.LocalDateTime;

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
    Long event_id;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    Long requester_id;
    @Enumerated(value = EnumType.STRING)
    RequestStatus status;

}