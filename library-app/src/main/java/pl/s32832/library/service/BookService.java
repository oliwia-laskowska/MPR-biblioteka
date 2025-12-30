package pl.s32832.library.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.s32832.library.dto.request.CreateBookRequest;
import pl.s32832.library.dto.request.UpdateBookRequest;
import pl.s32832.library.entity.Author;
import pl.s32832.library.entity.Book;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.repository.BookRepository;

import java.util.List;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public Book create(CreateBookRequest req) throws ValidationException {
        if (bookRepository.findByIsbn(req.getIsbn()).isPresent()) {
            throw new ValidationException("ISBN already exists: " + req.getIsbn());
        }
        Book book = new Book(req.getTitle(), req.getIsbn(), req.getTotalCopies());
        return bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public Book getById(Long id) throws NotFoundException {
        return bookRepository.findWithAuthorsById(id)
                .orElseThrow(() -> new NotFoundException("Book not found: " + id));
    }


    @Transactional(readOnly = true)
    public List<Book> getAll() {
        return bookRepository.findAll();
    }




    public Book update(Long id, UpdateBookRequest req) throws NotFoundException, BusinessRuleException {
        Book book = getById(id);
        int delta = req.getTotalCopies() - book.getTotalCopies();
        if (delta < 0) {
            int newTotal = req.getTotalCopies();
            int borrowed = book.getTotalCopies() - book.getAvailableCopies();
            if (newTotal < borrowed) {
                throw new BusinessRuleException("Cannot reduce totalCopies below currently borrowed count");
            }
        }
        book.setTitle(req.getTitle());
        book.setTotalCopies(req.getTotalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + delta);
        return bookRepository.save(book);
    }

    public void delete(Long id) throws NotFoundException {
        Book book = getById(id);
        bookRepository.delete(book);
    }

    public Book addAuthor(Long bookId, Long authorId) throws NotFoundException {
        Book book = getById(bookId);
        Author author = authorService.getById(authorId);
        book.getAuthors().add(author);
        author.getBooks().add(book);
        return bookRepository.save(book);
    }

    public Book removeAuthor(Long bookId, Long authorId) throws NotFoundException {
        Book book = getById(bookId);
        Author author = authorService.getById(authorId);
        book.getAuthors().remove(author);
        author.getBooks().remove(book);
        return bookRepository.save(book);
    }
}
