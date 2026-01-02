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

/**
 * Serwis obsługujący wypożyczenia (Loan).
 *
 * Najważniejsze zasady:
 * - można wypożyczyć tylko jeśli książka jest dostępna (availableCopies > 0)
 * - oddanie ustawia returnDate i zwiększa dostępność książki
 * - usunięcie wypożyczenia przywraca dostępność, jeśli wypożyczenie było aktywne
 */
@Service
@Transactional
public class LoanService {

    // domyślny czas wypożyczenia (14 dni)
    private static final int DEFAULT_LOAN_DAYS = 14;

    private final LoanRepository loanRepository;
    private final UserService userService;
    private final BookService bookService;
    private final DateTimeProvider dateTimeProvider;

    public LoanService(LoanRepository loanRepository,
                       UserService userService,
                       BookService bookService,
                       DateTimeProvider dateTimeProvider) {
        this.loanRepository = loanRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.dateTimeProvider = dateTimeProvider;
    }

    /**
     * Tworzy nowe wypożyczenie.
     * 1) pobieramy użytkownika i książkę (walidacja istnienia -> NotFoundException)
     * 2) sprawdzamy reguły biznesowe (czy książka jest dostępna)
     * 3) zmniejszamy dostępność książki
     * 4) tworzymy Loan z loanDate i dueDate (dziś + 14 dni)
     * 5) zapis przez repozytorium (ORM)
     */
    public Loan create(CreateLoanRequest req)
            throws NotFoundException, BusinessRuleException, ValidationException {

        User user = userService.getById(req.getUserId());
        Book book = bookService.getById(req.getBookId());

        // Nie wypożyczysz, jeśli brak dostępnych egzemplarzy
        if (book.getAvailableCopies() <= 0) {
            throw new BusinessRuleException("Book is not available for loan");
        }

        // Przykładowa walidacja email
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("User email invalid");
        }

        // Aktualizacja stanu książki (w tej samej transakcji)
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        LocalDate today = dateTimeProvider.today();
        Loan loan = new Loan(user, book, today, today.plusDays(DEFAULT_LOAN_DAYS));

        return loanRepository.save(loan);
    }

    /**
     * Pobiera wypożyczenie po ID.
     */
    @Transactional(readOnly = true)
    public Loan getById(Long id) throws NotFoundException {
        return loanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Loan not found: " + id));
    }

    /**
     * Pobiera wszystkie wypożyczenia.
     */
    @Transactional(readOnly = true)
    public List<Loan> getAll() {
        return loanRepository.findAll();
    }

    /**
     * Oddaje wypożyczenie:
     * - jeśli było już oddane -> BusinessRuleException
     * - ustawia returnDate
     * - zwiększa dostępność książki
     */
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

    /**
     * Usuwa wypożyczenie.
     * Jeśli wypożyczenie było aktywne (nie oddane),
     * to przywracamy dostępność książki.
     */
    public void delete(Long id) throws NotFoundException {
        Loan loan = getById(id);

        if (loan.getReturnDate() == null) {
            Book book = loan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
        }

        loanRepository.delete(loan);
    }
}
