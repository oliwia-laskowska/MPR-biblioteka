package pl.s32832.library.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.s32832.library.dto.request.CreateLoanRequest;
import pl.s32832.library.dto.response.LoanResponse;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.mapper.LoanMapper;
import pl.s32832.library.service.LoanService;

import java.util.List;

/**
 * REST Controller dla encji wypożyczenia.
 * Obsługuje: wypożyczenie książki, zwrot oraz CRUD.
 */
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Utworzenie nowego wypożyczenia.
     * Przykład: POST /api/loans z JSON { "userId": 1, "bookId": 2 }
     *
     * Może rzucić:
     * - NotFoundException (brak użytkownika/książki)
     * - BusinessRuleException (brak dostępnych egzemplarzy)
     * - ValidationException (np. dane użytkownika niepoprawne)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse create(@Valid @RequestBody CreateLoanRequest req)
            throws NotFoundException, BusinessRuleException, ValidationException {
        return LoanMapper.toResponse(loanService.create(req));
    }

    /**
     * Pobranie wypożyczenia po ID.
     */
    @GetMapping("/{id}")
    public LoanResponse get(@PathVariable Long id) throws NotFoundException {
        return LoanMapper.toResponse(loanService.getById(id));
    }

    /**
     * Pobranie listy wszystkich wypożyczeń.
     */
    @GetMapping
    public List<LoanResponse> getAll() {
        return loanService.getAll().stream()
                .map(LoanMapper::toResponse)
                .toList();
    }

    /**
     * Zwrot wypożyczenia.
     * Endpoint: PUT /api/loans/{id}/return
     *
     * Ustawia returnDate i zwiększa availableCopies książki.
     * Może rzucić BusinessRuleException jeśli wypożyczenie już było zwrócone.
     */
    @PutMapping("/{id}/return")
    public LoanResponse returnLoan(@PathVariable("id") Long id)
            throws NotFoundException, BusinessRuleException {
        return LoanMapper.toResponse(loanService.returnLoan(id));
    }

    /**
     * Usunięcie wypożyczenia.
     * Jeśli wypożyczenie nie było zwrócone, serwis przywraca dostępność książki.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws NotFoundException {
        loanService.delete(id);
    }
}
