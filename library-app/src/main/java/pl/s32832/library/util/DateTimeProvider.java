package pl.s32832.library.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateTimeProvider {
    /**
     * Zwraca dzisiejszą datę (systemową).
     */
    public LocalDate today() {
        return LocalDate.now();
    }
}
