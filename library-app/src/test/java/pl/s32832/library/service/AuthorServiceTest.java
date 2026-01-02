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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testy jednostkowe AuthorService.
 *
 * Sprawdzamy:
 * - czy tworzenie autora zapisuje encję w repozytorium,
 * - czy pobieranie po ID rzuca NotFoundException gdy brak encji,
 * - czy update zmienia dane i zapisuje encję,
 * - czy delete usuwa encję,
 * - czy getAll zwraca listę autorów.

 */
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

        when(authorRepository.save(any(Author.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Author a = authorService.create(req);

        assertEquals("Author", a.getName());
        verify(authorRepository).save(any(Author.class));
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
        when(authorRepository.save(any(Author.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UpdateAuthorRequest req = new UpdateAuthorRequest();
        req.setName("New");
        Author updated = authorService.update(1L, req);

        assertEquals("New", updated.getName());
        verify(authorRepository).save(a);
    }

    @Test
    void delete_shouldDeleteAuthor() throws Exception {
        Author a = new Author("ToDelete");
        when(authorRepository.findById(1L)).thenReturn(Optional.of(a));

        authorService.delete(1L);

        verify(authorRepository).delete(a);
    }

    @Test
    void getAll_shouldReturnList() {
        when(authorRepository.findAll()).thenReturn(List.of(
                new Author("A1"),
                new Author("A2")
        ));

        List<Author> authors = authorService.getAll();

        assertEquals(2, authors.size());
        verify(authorRepository).findAll();
    }
}
