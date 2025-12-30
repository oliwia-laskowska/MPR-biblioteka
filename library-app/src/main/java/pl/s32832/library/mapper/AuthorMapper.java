package pl.s32832.library.mapper;

import pl.s32832.library.dto.response.AuthorResponse;
import pl.s32832.library.entity.Author;

public class AuthorMapper {
    private AuthorMapper() {}

    public static AuthorResponse toResponse(Author a) {
        return new AuthorResponse(a.getId(), a.getName());
    }
}
