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

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody CreateBookRequest req) throws ValidationException {
        return BookMapper.toResponse(bookService.create(req));
    }

    @GetMapping("/{id}")
    public BookResponse get(@PathVariable Long id) throws NotFoundException {
        return BookMapper.toResponse(bookService.getById(id));
    }

    @GetMapping
    public List<BookResponse> getAll() {
        return bookService.getAll().stream().map(BookMapper::toResponse).toList();
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody UpdateBookRequest req)
            throws NotFoundException, BusinessRuleException {
        return BookMapper.toResponse(bookService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws NotFoundException {
        bookService.delete(id);
    }

    @PostMapping("/{bookId}/authors/{authorId}")
    public BookResponse addAuthor(@PathVariable Long bookId, @PathVariable Long authorId) throws NotFoundException {
        return BookMapper.toResponse(bookService.addAuthor(bookId, authorId));
    }

    @DeleteMapping("/{bookId}/authors/{authorId}")
    public BookResponse removeAuthor(@PathVariable Long bookId, @PathVariable Long authorId) throws NotFoundException {
        return BookMapper.toResponse(bookService.removeAuthor(bookId, authorId));
    }
}
