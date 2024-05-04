package ru.practicum.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.Status;

import java.util.Collection;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Collection<ParticipationRequest> findByEvent(Long eventId);

    Collection<ParticipationRequest> findByRequester(Long userId);

    ParticipationRequest findByEventAndRequester(Long eventId, Long requesterId);

    Collection<ParticipationRequest> findByEventAndStatus(Long eventId, Status status);
}
