package pl.s32832.library.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.s32832.library.dto.request.CreateAuthorRequest;
import pl.s32832.library.dto.request.UpdateAuthorRequest;
import pl.s32832.library.entity.Author;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.repository.AuthorRepository;

import java.util.List;

/**
 * Warstwa serwisowa dla Author.
 *
 * Tutaj znajduje się logika aplikacji związana z autorami.
 * Kontroler wywołuje metody serwisu, a serwis korzysta z repozytorium,
 * aby wykonywać operacje na bazie danych.
 */
@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;


    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Tworzy nowego autora.
     */
    public Author create(CreateAuthorRequest req) {
        return authorRepository.save(new Author(req.getName()));
    }

    /**
     * Pobiera autora po ID.
     * Jeśli nie istnieje — rzucamy własny checked exception NotFoundException,
     * readOnly = true -> optymalizacja dla zapytań tylko do odczytu.
     */
    @Transactional(readOnly = true)
    public Author getById(Long id) throws NotFoundException {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found: " + id));
    }

    /**
     * Zwraca listę wszystkich autorów.
     * readOnly = true, bo nie zmieniamy danych.
     */
    @Transactional(readOnly = true)
    public List<Author> getAll() {
        return authorRepository.findAll();
    }

    /**
     * Aktualizacja autora.
     * Najpierw pobieramy autora (albo rzucamy NotFoundException),
     * potem zmieniamy dane i zapisujemy.
     */
    public Author update(Long id, UpdateAuthorRequest req) throws NotFoundException {
        Author a = getById(id);
        a.setName(req.getName());
        return authorRepository.save(a);
    }

    /**
     * Usunięcie autora.
     * Najpierw sprawdzamy czy autor istnieje — jeśli nie, rzucamy NotFoundException.
     * Dzięki temu kontroler zwróci 404 zamiast np. 204 z niczym.
     */
    public void delete(Long id) throws NotFoundException {
        Author a = getById(id);
        authorRepository.delete(a);
    }
}
