package pl.s32832.library.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.s32832.library.dto.request.CreateBookRequest;
import pl.s32832.library.dto.request.UpdateBookRequest;
import pl.s32832.library.entity.Author;
import pl.s32832.library.entity.Book;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.repository.BookRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @Mock
    AuthorService authorService;

    @InjectMocks
    BookService bookService;

    // Sprawdza walidację: nie można stworzyć książki jeśli ISBN już istnieje
    @Test
    void create_shouldThrowWhenIsbnExists() {
        CreateBookRequest req = new CreateBookRequest();
        req.setTitle("T");
        req.setIsbn("123");
        req.setTotalCopies(1);

        when(bookRepository.findByIsbn("123"))
                .thenReturn(Optional.of(new Book("X", "123", 1)));

        assertThrows(ValidationException.class, () -> bookService.create(req));
        verify(bookRepository, never()).save(any());
    }

    // Sprawdza poprawne tworzenie książki
    @Test
    void create_shouldSaveWhenValid() throws Exception {
        CreateBookRequest req = new CreateBookRequest();
        req.setTitle("Clean Code");
        req.setIsbn("999");
        req.setTotalCopies(3);

        when(bookRepository.findByIsbn("999")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book created = bookService.create(req);

        assertEquals("Clean Code", created.getTitle());
        assertEquals("999", created.getIsbn());
        assertEquals(3, created.getTotalCopies());
        assertEquals(3, created.getAvailableCopies()); // na starcie tyle samo co totalCopies
        verify(bookRepository).save(any(Book.class));
    }

    // Sprawdza regułę biznesową: nie można zmniejszyć totalCopies poniżej liczby wypożyczonych
    @Test
    void update_shouldThrowWhenReducingBelowBorrowed() throws Exception {
        Book b = new Book("T", "123", 2);
        b.setAvailableCopies(0); // borrowed = 2

        // BookService.update() -> getById() -> findWithAuthorsById()
        when(bookRepository.findWithAuthorsById(1L)).thenReturn(Optional.of(b));

        UpdateBookRequest req = new UpdateBookRequest();
        req.setTitle("T2");
        req.setTotalCopies(1);

        assertThrows(BusinessRuleException.class, () -> bookService.update(1L, req));
        verify(bookRepository, never()).save(any());
    }

    // Sprawdza poprawny update: zmiana tytułu, totalCopies i przeliczenie availableCopies
    @Test
    void update_shouldUpdateAndAdjustAvailableCopies() throws Exception {
        Book b = new Book("Old", "123", 2);
        b.setAvailableCopies(1); // 1 wypożyczona

        when(bookRepository.findWithAuthorsById(1L)).thenReturn(Optional.of(b));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateBookRequest req = new UpdateBookRequest();
        req.setTitle("New");
        req.setTotalCopies(5); // delta = +3

        Book updated = bookService.update(1L, req);

        assertEquals("New", updated.getTitle());
        assertEquals(5, updated.getTotalCopies());
        assertEquals(4, updated.getAvailableCopies()); // 1 + 3
        verify(bookRepository).save(b);
    }

    // Sprawdza obsługę wyjątku: brak książki w bazie
    @Test
    void getById_shouldThrow() {
        when(bookRepository.findWithAuthorsById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookService.getById(1L));
    }

    // Sprawdza relację wiele-do-wielu: dodanie autora do książki
    @Test
    void addAuthor_shouldAddRelation() throws Exception {
        Book b = new Book("T", "123", 1);
        Author a = new Author("A");

        when(bookRepository.findWithAuthorsById(1L)).thenReturn(Optional.of(b));
        when(authorService.getById(2L)).thenReturn(a);
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book updated = bookService.addAuthor(1L, 2L);

        assertTrue(updated.getAuthors().contains(a));
        assertTrue(a.getBooks().contains(b));
    }

    // Sprawdza relację wiele-do-wielu: usunięcie autora z książki
    @Test
    void removeAuthor_shouldRemoveRelation() throws Exception {
        Book b = new Book("T", "123", 1);
        Author a = new Author("A");

        b.getAuthors().add(a);
        a.getBooks().add(b);

        when(bookRepository.findWithAuthorsById(1L)).thenReturn(Optional.of(b));
        when(authorService.getById(2L)).thenReturn(a);
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book updated = bookService.removeAuthor(1L, 2L);

        assertFalse(updated.getAuthors().contains(a));
        assertFalse(a.getBooks().contains(b));
    }

    // Sprawdza usuwanie książki (delete powinno wywołać repo.delete)
    @Test
    void delete_shouldDeleteBook() throws Exception {
        Book b = new Book("T", "123", 1);

        when(bookRepository.findWithAuthorsById(1L)).thenReturn(Optional.of(b));

        bookService.delete(1L);

        verify(bookRepository).delete(b);
    }
}
