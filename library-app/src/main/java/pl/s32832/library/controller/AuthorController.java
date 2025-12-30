package pl.s32832.library.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.s32832.library.dto.request.CreateAuthorRequest;
import pl.s32832.library.dto.request.UpdateAuthorRequest;
import pl.s32832.library.dto.response.AuthorResponse;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.mapper.AuthorMapper;
import pl.s32832.library.service.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(@Valid @RequestBody CreateAuthorRequest req) {
        return AuthorMapper.toResponse(authorService.create(req));
    }

    @GetMapping("/{id}")
    public AuthorResponse get(@PathVariable Long id) throws NotFoundException {
        return AuthorMapper.toResponse(authorService.getById(id));
    }

    @GetMapping
    public List<AuthorResponse> getAll() {
        return authorService.getAll().stream().map(AuthorMapper::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AuthorResponse update(@PathVariable Long id, @Valid @RequestBody UpdateAuthorRequest req) throws NotFoundException {
        return AuthorMapper.toResponse(authorService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws NotFoundException {
        authorService.delete(id);
    }
}
