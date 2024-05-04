package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.exception.ServerErrorException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepository;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql("/schema.sql")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createUserDuplicateEmail() {
        User userOne = User.builder()
                .email("email@email.com")
                .name("name")
                .build();

        UserDto userTwo = UserDto.builder()
                .email("email@email.com")
                .name("name1")
                .build();

        userRepository.save(userOne);

        assertThrows(ServerErrorException.class, () -> userService.createUser(userTwo));
    }

    @Test
    void deleteUser() {
        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getUser() {
        User user = User.builder()
                .email("email@email.com")
                .name("name1")
                .build();

        UserDto userSaved = UserDto.builder()
                .email("email@email.com")
                .name("name1")
                .id(1L)
                .build();

        userRepository.save(user);

        Collection<UserDto> userDto = userService.getUser(List.of(1L), 0, 10);

        assertThat(userDto.contains(userSaved));
    }
}