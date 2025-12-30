package pl.s32832.library.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.s32832.library.dto.request.CreateBookRequest;
import pl.s32832.library.dto.request.CreateAuthorRequest;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.service.AuthorService;
import pl.s32832.library.service.BookService;
import pl.s32832.library.dto.request.CreateLoanRequest;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.service.LoanService;
import pl.s32832.library.service.UserService;
import pl.s32832.library.dto.request.CreateProfileRequest;
import pl.s32832.library.dto.request.CreateUserRequest;
import pl.s32832.library.service.ProfileService;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.exception.NotFoundException;



@Controller
@RequestMapping("/web")
public class WebController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final LoanService loanService;
    private final UserService userService;
    private final ProfileService profileService;



    public WebController(BookService bookService,
                         AuthorService authorService,
                         LoanService loanService,
                         UserService userService,
                         ProfileService profileService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.loanService = loanService;
        this.userService = userService;
        this.profileService = profileService;
    }



    // Strona startowa
    @GetMapping
    public String home() {
        return "index";
    }

    // ---------- BOOKS ----------

    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("books", bookService.getAll());
        return "books/list";
    }

    @GetMapping("/books/new")
    public String newBook(Model model) {
        model.addAttribute("book", new CreateBookRequest());
        return "books/new";
    }

    @PostMapping("/books")
    public String createBook(@Valid @ModelAttribute("book") CreateBookRequest book,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        try {
            bookService.create(book);
            return "redirect:/web/books";
        } catch (ValidationException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "books/new";
        }
    }

    // ---------- AUTHORS ----------

    @GetMapping("/authors")
    public String authors(Model model) {
        model.addAttribute("authors", authorService.getAll());
        return "authors/list";
    }

    @GetMapping("/authors/new")
    public String newAuthor(Model model) {
        model.addAttribute("author", new CreateAuthorRequest());
        return "authors/new";
    }

    @PostMapping("/authors")
    public String createAuthor(@Valid @ModelAttribute("author") CreateAuthorRequest author,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "authors/new";
        }

        authorService.create(author);
        return "redirect:/web/authors";
    }

    @GetMapping("/books/{id}")
    public String bookDetails(@PathVariable Long id, Model model) throws Exception {
        var book = bookService.getById(id);
        model.addAttribute("book", book);
        model.addAttribute("allAuthors", authorService.getAll());
        return "books/details";
    }

    @PostMapping("/books/{bookId}/authors")
    public String addAuthorToBook(@PathVariable Long bookId,
                                  @RequestParam Long authorId) throws Exception {
        bookService.addAuthor(bookId, authorId);
        return "redirect:/web/books/" + bookId;
    }

    @PostMapping("/books/{bookId}/authors/{authorId}/remove")
    public String removeAuthorFromBook(@PathVariable Long bookId,
                                       @PathVariable Long authorId) throws Exception {
        bookService.removeAuthor(bookId, authorId);
        return "redirect:/web/books/" + bookId;
    }

    // ---------- BOOK DELETE ----------
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id) throws Exception {
        bookService.delete(id);
        return "redirect:/web/books";
    }

    // ---------- LOANS ----------

    @GetMapping("/loans")
    public String loans(Model model) {
        model.addAttribute("loans", loanService.getAll());
        return "loans/list";
    }

    @GetMapping("/loans/new")
    public String newLoan(Model model) {
        model.addAttribute("loan", new CreateLoanRequest());
        model.addAttribute("users", userService.getAll());
        model.addAttribute("books", bookService.getAll());
        return "loans/new";
    }

    @PostMapping("/loans")
    public String createLoan(@Valid @ModelAttribute("loan") CreateLoanRequest loan,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.getAll());
            model.addAttribute("books", bookService.getAll());
            return "loans/new";
        }

        try {
            loanService.create(loan);
            return "redirect:/web/loans";
        } catch (BusinessRuleException | ValidationException | NotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("users", userService.getAll());
            model.addAttribute("books", bookService.getAll());
            return "loans/new";
        }
    }

    @PostMapping("/loans/{id}/return")
    public String returnLoan(@PathVariable Long id, Model model) {
        try {
            loanService.returnLoan(id);
            return "redirect:/web/loans";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("loans", loanService.getAll());
            return "loans/list";
        }
    }

    @PostMapping("/loans/{id}/delete")
    public String deleteLoan(@PathVariable Long id, Model model) {
        try {
            loanService.delete(id);
            return "redirect:/web/loans";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("loans", loanService.getAll());
            return "loans/list";
        }
    }

    // ---------- USERS ----------

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users/list";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("user", new CreateUserRequest());
        return "users/new";
    }

    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute("user") CreateUserRequest user,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            return "users/new";
        }

        try {
            userService.create(user);
            return "redirect:/web/users";
        } catch (ValidationException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "users/new";
        }
    }

    @GetMapping("/users/{id}")
    public String userDetails(@PathVariable Long id, Model model) throws Exception {
        var user = userService.getById(id);
        model.addAttribute("user", user);

        // formularz do profilu (jeśli nie ma profilu)
        if (user.getProfile() == null) {
            model.addAttribute("profile", new CreateProfileRequest());
        }

        return "users/details";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) throws Exception {
        userService.delete(id);
        return "redirect:/web/users";
    }

// ---------- PROFILE ----------

    @PostMapping("/users/{userId}/profile")
    public String createProfile(@PathVariable Long userId,
                                @Valid @ModelAttribute("profile") CreateProfileRequest profile,
                                BindingResult bindingResult,
                                Model model) throws Exception {
        if (bindingResult.hasErrors()) {
            var user = userService.getById(userId);
            model.addAttribute("user", user);
            return "users/details";
        }

        try {
            profileService.create(userId, profile);
            return "redirect:/web/users/" + userId;
        } catch (BusinessRuleException ex) {
            var user = userService.getById(userId);
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", ex.getMessage());
            return "users/details";
        }
    }

    @PostMapping("/profiles/{profileId}/delete")
    public String deleteProfile(@PathVariable Long profileId) throws Exception {
        var profile = profileService.getById(profileId);
        Long userId = profile.getUser().getId();
        profileService.delete(profileId);
        return "redirect:/web/users/" + userId;
    }

}
