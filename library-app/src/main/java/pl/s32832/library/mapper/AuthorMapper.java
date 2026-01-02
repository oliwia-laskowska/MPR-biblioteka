package pl.s32832.library.mapper;

import pl.s32832.library.dto.response.AuthorResponse;
import pl.s32832.library.entity.Author;

/**
 * Mapper encji Author -> DTO AuthorResponse.
 *
 * Po co mapper?
 * - nie zwracamy encji bezpośrednio z REST API (encja = struktura bazy, może mieć relacje i dane techniczne)
 * - DTO jest "bezpieczne" i stabilne
 */
public class AuthorMapper {

    // blokada tworzenia obiektu klasy narzędziowej (utility class)
    private AuthorMapper() {}

    /**
     * Zamienia encję Author na obiekt AuthorResponse.
     */
    public static AuthorResponse toResponse(Author a) {
        return new AuthorResponse(
                a.getId(),
                a.getName()
        );
    }
}
