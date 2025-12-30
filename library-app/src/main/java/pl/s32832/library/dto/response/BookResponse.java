package pl.s32832.library.dto.response;

import java.util.Set;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        int totalCopies,
        int availableCopies,
        Set<Long> authorIds
) {}
