package pl.s32832.library.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.s32832.library.dto.request.CreateAuthorRequest;
import pl.s32832.library.dto.request.UpdateAuthorRequest;
import pl.s32832.library.entity.Author;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.repository.AuthorRepository;

import java.util.List;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author create(CreateAuthorRequest req) {
        return authorRepository.save(new Author(req.getName()));
    }

    @Transactional(readOnly = true)
    public Author getById(Long id) throws NotFoundException {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Author> getAll() {
        return authorRepository.findAll();
    }

    public Author update(Long id, UpdateAuthorRequest req) throws NotFoundException {
        Author a = getById(id);
        a.setName(req.getName());
        return authorRepository.save(a);
    }

    public void delete(Long id) throws NotFoundException {
        Author a = getById(id);
        authorRepository.delete(a);
    }
}
