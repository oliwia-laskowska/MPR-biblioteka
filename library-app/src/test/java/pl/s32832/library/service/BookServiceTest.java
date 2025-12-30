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

    @Test
    void create_shouldThrowWhenIsbnExists() {
        CreateBookRequest req = new CreateBookRequest();
        req.setTitle("T");
        req.setIsbn("123");
        req.setTotalCopies(1);

        when(bookRepository.findByIsbn("123"))
                .thenReturn(Optional.of(new Book("X", "123", 1)));

        assertThrows(ValidationException.class, () -> bookService.create(req));
    }

    @Test
    void update_shouldThrowWhenReducingBelowBorrowed() throws Exception {
        Book b = new Book("T", "123", 2);
        b.setAvailableCopies(0); // borrowed = totalCopies - availableCopies = 2

        // BookService.update() używa getById(), a getById() używa findWithAuthorsById()
        when(bookRepository.findWithAuthorsById(1L)).thenReturn(Optional.of(b));

        UpdateBookRequest req = new UpdateBookRequest();
        req.setTitle("T2");
        req.setTotalCopies(1);

        assertThrows(BusinessRuleException.class, () -> bookService.update(1L, req));
    }

    @Test
    void getById_shouldThrow() {
        when(bookRepository.findWithAuthorsById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookService.getById(1L));
    }

    @Test
    void addAuthor_shouldAddRelation() throws Exception {
        Book b = new Book("T", "123", 1);
        Author a = new Author("A");

        // BookService.addAuthor() -> getById() -> findWithAuthorsById()
        when(bookRepository.findWithAuthorsById(1L)).thenReturn(Optional.of(b));
        when(authorService.getById(2L)).thenReturn(a);

        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book updated = bookService.addAuthor(1L, 2L);

        assertTrue(updated.getAuthors().contains(a));
        assertTrue(a.getBooks().contains(b));
    }

    @Test
    void removeAuthor_shouldRemoveRelation() throws Exception {
        Book b = new Book("T", "123", 1);
        Author a = new Author("A");
        b.getAuthors().add(a);
        a.getBooks().add(b);

        // BookService.removeAuthor() -> getById() -> findWithAuthorsById()
        when(bookRepository.findWithAuthorsById(1L)).thenReturn(Optional.of(b));
        when(authorService.getById(2L)).thenReturn(a);

        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book updated = bookService.removeAuthor(1L, 2L);

        assertFalse(updated.getAuthors().contains(a));
        assertFalse(a.getBooks().contains(b));
    }
}
