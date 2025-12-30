package pl.s32832.library.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.s32832.library.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    @EntityGraph(attributePaths = {"authors"})
    Optional<Book> findWithAuthorsById(Long id);

    @Override
    @EntityGraph(attributePaths = {"authors"})
    List<Book> findAll();
}
