package pl.s32832.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.s32832.library.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
