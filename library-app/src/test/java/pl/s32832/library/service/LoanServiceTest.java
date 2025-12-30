package pl.s32832.library.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.s32832.library.dto.request.CreateLoanRequest;
import pl.s32832.library.entity.Book;
import pl.s32832.library.entity.Loan;
import pl.s32832.library.entity.User;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.repository.LoanRepository;
import pl.s32832.library.util.DateTimeProvider;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    LoanRepository loanRepository;
    @Mock
    UserService userService;
    @Mock
    BookService bookService;
    @Mock
    DateTimeProvider dateTimeProvider;

    @InjectMocks
    LoanService loanService;

    @Test
    void create_shouldThrowWhenBookUnavailable() throws Exception {
        CreateLoanRequest req = new CreateLoanRequest();
        req.setUserId(1L);
        req.setBookId(2L);

        User u = new User("a@b.com", "X");
        Book b = new Book("T", "123", 1);
        b.setAvailableCopies(0);

        when(userService.getById(1L)).thenReturn(u);
        when(bookService.getById(2L)).thenReturn(b);

        assertThrows(BusinessRuleException.class, () -> loanService.create(req));
    }

    @Test
    void returnLoan_shouldSetReturnDate() throws Exception {
        User u = new User("a@b.com", "X");
        Book b = new Book("T", "123", 1);
        b.setAvailableCopies(0);

        Loan loan = new Loan(u, b, LocalDate.now(), LocalDate.now().plusDays(14));

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(dateTimeProvider.today()).thenReturn(LocalDate.of(2025,1,1));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan returned = loanService.returnLoan(1L);

        assertEquals(LocalDate.of(2025,1,1), returned.getReturnDate());
        assertEquals(1, b.getAvailableCopies());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> loanService.getById(1L));
    }

    @Test
    void create_shouldCreateLoanWhenBookAvailable() throws Exception {
        CreateLoanRequest req = new CreateLoanRequest();
        req.setUserId(1L);
        req.setBookId(2L);

        User u = new User("a@b.com", "X");
        Book b = new Book("T", "123", 1);
        b.setAvailableCopies(1);

        when(userService.getById(1L)).thenReturn(u);
        when(bookService.getById(2L)).thenReturn(b);
        when(dateTimeProvider.today()).thenReturn(LocalDate.of(2025, 1, 1));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        Loan loan = loanService.create(req);

        assertNotNull(loan);
        assertEquals(LocalDate.of(2025, 1, 1), loan.getLoanDate());
        assertEquals(0, b.getAvailableCopies()); // one copy borrowed
    }

    @Test
    void returnLoan_shouldThrowIfAlreadyReturned() throws Exception {
        User u = new User("a@b.com", "X");
        Book b = new Book("T", "123", 1);
        Loan loan = new Loan(u, b, LocalDate.now(), LocalDate.now().plusDays(14));
        loan.setReturnDate(LocalDate.now());

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(BusinessRuleException.class, () -> loanService.returnLoan(1L));
    }

}
