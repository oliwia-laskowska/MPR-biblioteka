package pl.s32832.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.s32832.library.entity.Author;

/**
 * Repository dla encji Author.
 *
 * Warstwa dostępu do bazy danych.
 * Metody CRUD bez pisania SQL:
 * - save()
 * - findById()
 * - findAll()
 * - delete()
 *
 * Spring Data JPA generuje implementację automatycznie w runtime.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
