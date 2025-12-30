package pl.s32832.library.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.s32832.library.dto.request.CreateUserRequest;
import pl.s32832.library.dto.request.UpdateUserRequest;
import pl.s32832.library.entity.User;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    void create_shouldSaveUser() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("a@b.com");
        req.setFullName("John Doe");

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.create(req);

        assertEquals("a@b.com", created.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_shouldThrowWhenEmailExists() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("a@b.com");
        req.setFullName("John Doe");

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(new User("a@b.com", "X")));

        assertThrows(ValidationException.class, () -> userService.create(req));
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void update_shouldChangeName() throws Exception {
        User u = new User("a@b.com", "Old");
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserRequest req = new UpdateUserRequest();
        req.setFullName("New");

        User updated = userService.update(1L, req);

        assertEquals("New", updated.getFullName());
    }
}
