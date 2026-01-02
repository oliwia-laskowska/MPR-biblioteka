package pl.s32832.library.exception;

/**
 * Checked exception rzucany, gdy nie znaleziono zasobu w bazie danych.
 *
 * Przykład użycia:
 * - szukamy książki po ID, ale nie istnieje -> throw new NotFoundException(...)
 *
 * ApiExceptionHandler mapuje ten wyjątek na odpowiedź HTTP 404.
 */
public class NotFoundException extends Exception {

    public NotFoundException(String message) {
        super(message);
    }
}
