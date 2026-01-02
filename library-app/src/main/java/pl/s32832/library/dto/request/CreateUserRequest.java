package pl.s32832.library.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO do utworzenia nowego użytkownika biblioteki.
 * Używane w:
 * - REST: POST /api/users
 * - GUI (Thymeleaf): formularz dodawania użytkownika (users/new.html)
 *
 * Walidacja:
 * - email musi być poprawnym adresem email i nie może być pusty
 * - fullName nie może być pusty
 *
 * Dodatkowo w UserService.create() jest sprawdzane:
 * - czy email nie istnieje już w bazie (rzucany ValidationException - checked)
 */
public class CreateUserRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String fullName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
