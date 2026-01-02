package pl.s32832.library.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO do tworzenia nowej książki.
 * Używane w:
 * - REST: POST /api/books
 * - GUI (Thymeleaf): formularz dodawania książki
 *
 * Walidacje są wykonywane przez Bean Validation (@Valid),
 * a błędy łapane w ApiExceptionHandler / WebController.
 */
public class CreateBookRequest {

    /**
     * Tytuł książki.
     */
    @NotBlank
    private String title;

    /**
     * Numer ISBN książki.
     * Dodatkowo aplikacja sprawdza unikalność ISBN w BookService
     * i rzuca ValidationException gdy ISBN już istnieje.
     */
    @NotBlank
    private String isbn;

    /**
     * Całkowita liczba egzemplarzy książki.
     */
    @Min(1)
    private int totalCopies;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }
}
