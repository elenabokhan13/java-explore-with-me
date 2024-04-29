package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ServerErrorException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepository;
import ru.practicum.validator.UserValidator;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User current = userRepository.findByEmail(userDto.getEmail());
        if (current != null) {
            throw new ServerErrorException("User with such email already exists");
        }

        User user = userRepository.save(UserMapper.dtoToUser(userDto));
        return UserMapper.userToDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        UserValidator.validateUserExists(userRepository, userId);
        userRepository.deleteById(userId);
    }

    @Override
    public Collection<UserDto> getUser(List<Long> ids, int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        Page<User> response;
        if (ids.size() == 0) {
            response = userRepository.findAll(pageable);
        } else {
            response = userRepository.findByIdIn(ids, pageable);
        }
        return response.stream()
                .map(UserMapper::userToDto).collect(Collectors.toList());
    }
}
