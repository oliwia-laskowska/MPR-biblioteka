package pl.s32832.library.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.s32832.library.dto.request.CreateBookRequest;
import pl.s32832.library.dto.request.UpdateBookRequest;
import pl.s32832.library.dto.response.BookResponse;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.mapper.BookMapper;
import pl.s32832.library.service.BookService;

import java.util.List;

/**
 * REST Controller dla encji Book.
 * Udostępnia CRUD + operacje na relacji Book <-> Author (many-to-many).
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Dodanie nowej książki.
     * @Valid uruchamia walidację pól z CreateBookRequest.
     * Może rzucić ValidationException np. gdy ISBN już istnieje.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody CreateBookRequest req) throws ValidationException {
        return BookMapper.toResponse(bookService.create(req));
    }

    /**
     * Pobranie książki po ID.
     * Jeśli nie ma takiej książki -> NotFoundException.
     */
    @GetMapping("/{id}")
    public BookResponse get(@PathVariable Long id) throws NotFoundException {
        return BookMapper.toResponse(bookService.getById(id));
    }

    /**
     * Pobranie listy wszystkich książek.
     */
    @GetMapping
    public List<BookResponse> getAll() {
        return bookService.getAll().stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    /**
     * Aktualizacja książki.
     * Może rzucić BusinessRuleException (np. próba zmniejszenia totalCopies poniżej liczby wypożyczeń).
     */
    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id,
                               @Valid @RequestBody UpdateBookRequest req)
            throws NotFoundException, BusinessRuleException {
        return BookMapper.toResponse(bookService.update(id, req));
    }

    /**
     * Usunięcie książki po ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws NotFoundException {
        bookService.delete(id);
    }

    /**
     * Dodanie autora do książki (relacja many-to-many).
     * Endpoint: POST /api/books/{bookId}/authors/{authorId}
     */
    @PostMapping("/{bookId}/authors/{authorId}")
    public BookResponse addAuthor(@PathVariable Long bookId,
                                  @PathVariable Long authorId) throws NotFoundException {
        return BookMapper.toResponse(bookService.addAuthor(bookId, authorId));
    }

    /**
     * Usunięcie autora z książki (relacja many-to-many).
     * Endpoint: DELETE /api/books/{bookId}/authors/{authorId}
     */
    @DeleteMapping("/{bookId}/authors/{authorId}")
    public BookResponse removeAuthor(@PathVariable Long bookId,
                                     @PathVariable Long authorId) throws NotFoundException {
        return BookMapper.toResponse(bookService.removeAuthor(bookId, authorId));
    }
}
