package pl.s32832.library.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.s32832.library.entity.Book;

import java.util.List;
import java.util.Optional;

/**
 * Repository dla encji Book.
 *
 * Odpowiada za operacje na tabeli books (CRUD) i daje dostęp do danych
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Wyszukanie książki po ISBN.
     *
     * Spring Data JPA na podstawie nazwy metody samo tworzy zapytanie:
     * SELECT b FROM Book b WHERE b.isbn = :isbn
     *
     * Zwracamy Optional, bo książka może nie istnieć.
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Pobranie książki po ID razem z autorami (Many-to-Many).
     *
     * @EntityGraph(attributePaths = {"authors"}) powoduje, że relacja "authors"
     * zostanie dociągnięta od razu w jednym zapytaniu,
     * zamiast Lazy-loading po fakcie.
     *
     * Dzięki temu unikamy problemu wielu dodatkowych zapytań
     */
    @EntityGraph(attributePaths = {"authors"})
    Optional<Book> findWithAuthorsById(Long id);

    /**
     * Nadpisujemy findAll() i dokładamy EntityGraph,
     * żeby lista książek od razu miała dociągniętych autorów.

     */
    @Override
    @EntityGraph(attributePaths = {"authors"})
    List<Book> findAll();
}
