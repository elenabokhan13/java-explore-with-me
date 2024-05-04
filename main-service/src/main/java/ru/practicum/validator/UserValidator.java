package ru.practicum.validator;

import ru.practicum.exception.AccessForbiddenError;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ServerErrorException;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepository;

import java.util.Objects;

public class UserValidator {
    public static User validateUserExists(UserRepository userRepository, Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + id + " was not found"));

    }

    public static void validateUserAccessToEvent(Long userId, Long eventId) {
        if (!Objects.equals(eventId, userId)) {
            throw new ServerErrorException("User with id=" + userId + " doesn't have access to the event with id="
                    + eventId);
        }
    }

    public static void validateUserAccessToRequest(Long requestId, Long userId) {
        if (!Objects.equals(requestId, userId)) {
            throw new AccessForbiddenError("User with id=" + userId + " doesn't have access to participation " +
                    "request with id=" + requestId);
        }
    }
}
