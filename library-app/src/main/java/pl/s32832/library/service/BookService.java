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

/**
 * Serwis obsługujący logikę biznesową książek (Book).
 *
 * - Kontroler wywołuje metody tego serwisu
 * - Serwis waliduje reguły biznesowe (np. ISBN unikalny, liczba egzemplarzy)
 * - Zapis/odczyt danych odbywa się przez BookRepository
 */
@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    /**
     * Tworzy nową książkę.
     * Walidacja biznesowa: ISBN musi być unikalny.
     */
    public Book create(CreateBookRequest req) throws ValidationException {
        if (bookRepository.findByIsbn(req.getIsbn()).isPresent()) {
            throw new ValidationException("ISBN already exists: " + req.getIsbn());
        }

        // Tworzymy encję Book na podstawie requestu i zapisujemy do bazy przez ORM
        Book book = new Book(req.getTitle(), req.getIsbn(), req.getTotalCopies());
        return bookRepository.save(book);
    }

    /**
     * Pobiera książkę po ID.
     * Używamy specjalnej metody repozytorium z EntityGraph,
     * żeby od razu pobrać autorów (ManyToMany)
     * readOnly = true -> optymalizacja dla odczytu.
     */
    @Transactional(readOnly = true)
    public Book getById(Long id) throws NotFoundException {
        return bookRepository.findWithAuthorsById(id)
                .orElseThrow(() -> new NotFoundException("Book not found: " + id));
    }

    /**
     * Pobiera wszystkie książki.
     * W repozytorium findAll() też jest oznaczone EntityGraph -> autorzy będą dociągnięci.
     */
    @Transactional(readOnly = true)
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    /**
     * Aktualizuje książkę.
     *
     * Najważniejsza reguła biznesowa:
     * - nie można zmniejszyć totalCopies poniżej liczby wypożyczonych egzemplarzy.
     * borrowed = totalCopies - availableCopies
     */
    public Book update(Long id, UpdateBookRequest req) throws NotFoundException, BusinessRuleException {
        Book book = getById(id);

        // delta pokazuje o ile zmienia się całkowita liczba egzemplarzy
        int delta = req.getTotalCopies() - book.getTotalCopies();

        // Jeśli zmniejszamy totalCopies, sprawdzamy czy nie spadnie poniżej wypożyczonych
        if (delta < 0) {
            int newTotal = req.getTotalCopies();
            int borrowed = book.getTotalCopies() - book.getAvailableCopies();
            if (newTotal < borrowed) {
                throw new BusinessRuleException("Cannot reduce totalCopies below currently borrowed count");
            }
        }

        // Aktualizacja pól encji
        book.setTitle(req.getTitle());
        book.setTotalCopies(req.getTotalCopies());

        // availableCopies też muszą się zmienić o tę samą deltę
        // np. total 5 -> 7 => available +2
        // np. total 5 -> 3 => available -2 (ale tylko jeśli reguła biznesowa pozwala)
        book.setAvailableCopies(book.getAvailableCopies() + delta);

        return bookRepository.save(book);
    }

    /**
     * Usuwa książkę po ID.
     * Jeśli nie istnieje -> NotFoundException.
     */
    public void delete(Long id) throws NotFoundException {
        Book book = getById(id);
        bookRepository.delete(book);
    }

    /**
     * Dodaje autora do książki (relacja ManyToMany).
     * Uzupełniamy obie strony relacji:
     * - book.authors
     * - author.books
     */
    public Book addAuthor(Long bookId, Long authorId) throws NotFoundException {
        Book book = getById(bookId);
        Author author = authorService.getById(authorId);

        book.getAuthors().add(author);
        author.getBooks().add(book);

        return bookRepository.save(book);
    }

    /**
     * Usuwa autora z książki (relacja ManyToMany).
     * Również aktualizujemy obie strony relacji.
     */
    public Book removeAuthor(Long bookId, Long authorId) throws NotFoundException {
        Book book = getById(bookId);
        Author author = authorService.getById(authorId);

        book.getAuthors().remove(author);
        author.getBooks().remove(book);

        return bookRepository.save(book);
    }
}
