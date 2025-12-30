package pl.s32832.library.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.s32832.library.dto.request.CreateLoanRequest;
import pl.s32832.library.entity.Book;
import pl.s32832.library.entity.Loan;
import pl.s32832.library.entity.User;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.repository.LoanRepository;
import pl.s32832.library.util.DateTimeProvider;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LoanService {

    private static final int DEFAULT_LOAN_DAYS = 14;

    private final LoanRepository loanRepository;
    private final UserService userService;
    private final BookService bookService;
    private final DateTimeProvider dateTimeProvider;

    public LoanService(LoanRepository loanRepository, UserService userService, BookService bookService, DateTimeProvider dateTimeProvider) {
        this.loanRepository = loanRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.dateTimeProvider = dateTimeProvider;
    }

    public Loan create(CreateLoanRequest req) throws NotFoundException, BusinessRuleException, ValidationException {
        User user = userService.getById(req.getUserId());
        Book book = bookService.getById(req.getBookId());

        if (book.getAvailableCopies() <= 0) {
            throw new BusinessRuleException("Book is not available for loan");
        }

        // example additional validation
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("User email invalid");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        LocalDate today = dateTimeProvider.today();
        Loan loan = new Loan(user, book, today, today.plusDays(DEFAULT_LOAN_DAYS));
        return loanRepository.save(loan);
    }

    @Transactional(readOnly = true)
    public Loan getById(Long id) throws NotFoundException {
        return loanRepository.findById(id).orElseThrow(() -> new NotFoundException("Loan not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Loan> getAll() {
        return loanRepository.findAll();
    }

    public Loan returnLoan(Long id) throws NotFoundException, BusinessRuleException {
        Loan loan = getById(id);
        if (loan.getReturnDate() != null) {
            throw new BusinessRuleException("Loan already returned");
        }
        loan.setReturnDate(dateTimeProvider.today());
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return loanRepository.save(loan);
    }

    public void delete(Long id) throws NotFoundException {
        Loan loan = getById(id);
        // if not returned, restore availability
        if (loan.getReturnDate() == null) {
            Book book = loan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
        }
        loanRepository.delete(loan);
    }
}
