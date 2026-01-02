package pl.s32832.library.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO do tworzenia nowego autora.
 * Używane w:
 * - REST: POST /api/authors
 * - GUI (Thymeleaf): formularz dodawania autora
 */
public class CreateAuthorRequest {

    /**
     * Imię i nazwisko autora.
     */
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
