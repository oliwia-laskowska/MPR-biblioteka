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

    // Create: zapisuje nowego usera jeśli email nie istnieje
    @Test
    void create_shouldSaveUser() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("a@b.com");
        req.setFullName("John Doe");

        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.create(req);

        assertNotNull(created);
        assertEquals("a@b.com", created.getEmail());
        assertEquals("John Doe", created.getFullName());
        verify(userRepository).save(any(User.class));
    }

    // Create: jeśli email już istnieje -> ValidationException
    @Test
    void create_shouldThrowWhenEmailExists() {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("a@b.com");
        req.setFullName("John Doe");

        when(userRepository.findByEmail("a@b.com"))
                .thenReturn(Optional.of(new User("a@b.com", "X")));

        assertThrows(ValidationException.class, () -> userService.create(req));

        verify(userRepository, never()).save(any());
    }

    // GetById: zwraca usera jeśli istnieje
    @Test
    void getById_shouldReturnUser() throws Exception {
        User u = new User("a@b.com", "John Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        User found = userService.getById(1L);

        assertNotNull(found);
        assertEquals("a@b.com", found.getEmail());
        assertEquals("John Doe", found.getFullName());
    }

    // GetById: jeśli user nie istnieje -> NotFoundException
    @Test
    void getById_shouldThrowWhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    // Update: zmienia fullName jeśli user istnieje
    @Test
    void update_shouldChangeName() throws Exception {
        User u = new User("a@b.com", "Old");

        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateUserRequest req = new UpdateUserRequest();
        req.setFullName("New");

        User updated = userService.update(1L, req);

        assertEquals("New", updated.getFullName());
        verify(userRepository).save(u);
    }

    // Update: jeśli user nie istnieje -> NotFoundException
    @Test
    void update_shouldThrowWhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateUserRequest req = new UpdateUserRequest();
        req.setFullName("New");

        assertThrows(NotFoundException.class, () -> userService.update(1L, req));
        verify(userRepository, never()).save(any());
    }

    // Delete: usuwa usera jeśli istnieje
    @Test
    void delete_shouldDeleteUser() throws Exception {
        User u = new User("a@b.com", "John Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        userService.delete(1L);

        verify(userRepository).delete(u);
    }

    // Delete: jeśli user nie istnieje -> NotFoundException
    @Test
    void delete_shouldThrowWhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(1L));
        verify(userRepository, never()).delete(any());
    }
}
