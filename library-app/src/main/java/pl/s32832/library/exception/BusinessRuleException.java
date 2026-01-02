package pl.s32832.library.exception;

/**
 * Custom checked exception.
 * Rzucamy go wtedy, gdy użytkownik próbuje wykonać akcję,
 * która łamie reguły biznesowe aplikacji (np. wypożyczenie niedostępnej książki).
 *
 * Checked exception = wymusza obsługę w kodzie (throws / try-catch),
 * dlatego kontrolujemy, gdzie i w jaki sposób błąd jest zwracany do klienta.
 */
public class BusinessRuleException extends Exception {

    public BusinessRuleException(String message) {
        super(message);
    }
}
