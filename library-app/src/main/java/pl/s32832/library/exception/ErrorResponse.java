package pl.s32832.library.exception;

import java.time.Instant;

/**
 * Standardowa odpowiedź błędu zwracana przez aplikację w formacie JSON.
 * Dzięki temu wszystkie błędy mają jeden spójny format.
 */
public record ErrorResponse(
        Instant timestamp, // kiedy wystąpił błąd
        int status,        // kod HTTP (np. 400 / 404 / 500)
        String error,      // krótki kod błędu (np. NOT_FOUND)
        String message,    // opis błędu dla użytkownika
        String path        // endpoint, na którym wystąpił błąd
) {}
