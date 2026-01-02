package pl.s32832.library.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO do operacji zwrotu wypożyczenia.
 *
 * Użycie:
 * - REST: PUT /api/loans/{id}/return
 *
 * confirm = true oznacza, że użytkownik potwierdził zwrot.
 * Dzięki temu endpoint nie zwróci książki „przypadkiem” (np. błędne kliknięcie).
 */
public class ReturnLoanRequest {

    @NotNull
    private Boolean confirm;

    public Boolean getConfirm() {
        return confirm;
    }

    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }
}
