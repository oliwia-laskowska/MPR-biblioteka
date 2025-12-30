package pl.s32832.library.dto.response;

import java.time.LocalDate;

public record LoanResponse(
        Long id,
        Long userId,
        Long bookId,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnDate
) {}
