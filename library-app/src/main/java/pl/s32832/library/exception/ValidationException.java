package pl.s32832.library.exception;

/**
 * Checked exception używany do walidacji biznesowej (nie bean validation).
 *
 * Przykłady:
 * - próba dodania książki z istniejącym ISBN
 * - próba dodania użytkownika z emailem, który już jest w bazie
 *
 * ApiExceptionHandler mapuje ten wyjątek na HTTP 400.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
