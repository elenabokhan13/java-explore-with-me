package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.Valid;
import java.util.Collection;

import static ru.practicum.constant.Constant.USER_EVENT_URL;
import static ru.practicum.constant.Constant.USER_REQUEST;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class RequestController {
    @Autowired
    private final RequestService requestService;

    @GetMapping(path = USER_EVENT_URL + "/{eventId}/requests")
    public Collection<ParticipationRequestDto> getParticipationRequests(@PathVariable Long userId,
                                                                        @PathVariable Long eventId) {
        log.info("Получен запрос к эндпойнту /users/{}/events/{} для получения списка заявок на участие",
                userId, eventId);
        return requestService.getParticipationRequest(userId, eventId);
    }

    @PatchMapping(path = USER_EVENT_URL + "/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @RequestBody @Valid EventRequestStatusUpdateRequest event) {
        log.info("Получен запрос к эндпойнту /users/{}/events/{} для обновления статуса списка заявок на участие",
                userId, eventId);
        return requestService.updateParticipationRequest(userId, eventId, event);
    }

    @GetMapping(path = USER_REQUEST)
    public Collection<ParticipationRequestDto> getUserParticipationRequests(@PathVariable Long userId) {
        log.info("Получен запрос к эндпойнту /users/{}/requests для получения списка заявок на участие пользователя с id {}",
                userId, userId);
        return requestService.getUserParticipationRequests(userId);
    }

    @PostMapping(path = USER_REQUEST)
    public ResponseEntity<ParticipationRequestDto> createParticipationRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Получен запрос к эндпойнту /users/{}/requests для создания заявки на участие пользователя с id {} " +
                "в мероприятии с id {}", userId, userId, eventId);
        return new ResponseEntity<>(requestService.createParticipationRequest(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping(path = USER_REQUEST + "/{requestId}/cancel")
    public ParticipationRequestDto cancelParticipationRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Получен запрос к эндпойнту /users/{}/requests/{}/cancel для отмены заявки на участие в мероприятии " +
                "с id {} пользователя с id {}", userId, requestId, requestId, userId);
        return requestService.cancelParticipationRequest(userId, requestId);
    }
}
