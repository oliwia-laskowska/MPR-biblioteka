package pl.s32832.library.mapper;

import pl.s32832.library.dto.response.LoanResponse;
import pl.s32832.library.entity.Loan;

public class LoanMapper {
    private LoanMapper() {}

    public static LoanResponse toResponse(Loan l) {
        return new LoanResponse(
                l.getId(),
                l.getUser().getId(),
                l.getBook().getId(),
                l.getLoanDate(),
                l.getDueDate(),
                l.getReturnDate()
        );
    }
}
