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

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse create(@Valid @RequestBody CreateLoanRequest req)
            throws NotFoundException, BusinessRuleException, ValidationException {
        return LoanMapper.toResponse(loanService.create(req));
    }

    @GetMapping("/{id}")
    public LoanResponse get(@PathVariable Long id) throws NotFoundException {
        return LoanMapper.toResponse(loanService.getById(id));
    }

    @GetMapping
    public List<LoanResponse> getAll() {
        return loanService.getAll().stream().map(LoanMapper::toResponse).toList();
    }

    @PutMapping("/{id}/return")
    public LoanResponse returnLoan(@PathVariable("id") Long id) throws NotFoundException, BusinessRuleException {
        return LoanMapper.toResponse(loanService.returnLoan(id));
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws NotFoundException {
        loanService.delete(id);
    }
}
