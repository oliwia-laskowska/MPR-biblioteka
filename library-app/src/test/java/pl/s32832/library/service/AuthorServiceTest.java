package pl.s32832.library.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.s32832.library.dto.request.CreateAuthorRequest;
import pl.s32832.library.dto.request.UpdateAuthorRequest;
import pl.s32832.library.entity.Author;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.repository.AuthorRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    AuthorRepository authorRepository;

    @InjectMocks
    AuthorService authorService;

    @Test
    void create_shouldSave() {
        CreateAuthorRequest req = new CreateAuthorRequest();
        req.setName("Author");

        when(authorRepository.save(any(Author.class))).thenAnswer(inv -> inv.getArgument(0));

        Author a = authorService.create(req);

        assertEquals("Author", a.getName());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> authorService.getById(1L));
    }

    @Test
    void update_shouldUpdateName() throws Exception {
        Author a = new Author("Old");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(a));
        when(authorRepository.save(any(Author.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateAuthorRequest req = new UpdateAuthorRequest();
        req.setName("New");

        Author updated = authorService.update(1L, req);
        assertEquals("New", updated.getName());
    }
}
