package pl.s32832.library.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO do utworzenia wypożyczenia książki.
 * Używane w:
 * - REST: POST /api/loans
 * - GUI (Thymeleaf): formularz "Nowe wypożyczenie"
 *
 * Zawiera tylko identyfikatory użytkownika i książki,
 * bo reszta danych (daty, termin zwrotu, returnDate)
 * jest wyliczana w LoanService.
 */
public class CreateLoanRequest {

    /**
     * ID użytkownika, który wypożycza książkę.
     */
    @NotNull
    private Long userId;

    /**
     * ID książki, która ma zostać wypożyczona.
     */
    @NotNull
    private Long bookId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
