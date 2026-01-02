package pl.s32832.library.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.s32832.library.dto.request.CreateAuthorRequest;
import pl.s32832.library.dto.request.CreateBookRequest;
import pl.s32832.library.dto.request.CreateLoanRequest;
import pl.s32832.library.dto.request.CreateProfileRequest;
import pl.s32832.library.dto.request.CreateUserRequest;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.service.AuthorService;
import pl.s32832.library.service.BookService;
import pl.s32832.library.service.LoanService;
import pl.s32832.library.service.ProfileService;
import pl.s32832.library.service.UserService;

/**
 * Kontroler dla GUI (Thymeleaf).
 */
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


    // HOME
    /**
     * Strona startowa GUI: templates/index.html
     */
    @GetMapping
    public String home() {
        return "index";
    }


    // BOOKS
    /**
     * Lista książek: templates/books/list.html
     */
    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("books", bookService.getAll());
        return "books/list";
    }

    /**
     * Formularz dodawania książki: templates/books/new.html
     */
    @GetMapping("/books/new")
    public String newBook(Model model) {
        model.addAttribute("book", new CreateBookRequest());
        return "books/new";
    }

    /**
     * Obsługa wysłania formularza dodawania książki.
     * - @Valid odpala walidacje z DTO
     * - BindingResult zbiera błędy walidacji
     */
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

    /**
     * Szczegóły książki + lista autorów do wyboru.
     * Widok: templates/books/details.html
     */
    @GetMapping("/books/{id}")
    public String bookDetails(@PathVariable Long id, Model model) throws Exception {
        var book = bookService.getById(id);
        model.addAttribute("book", book);
        model.addAttribute("allAuthors", authorService.getAll());
        return "books/details";
    }

    /**
     * Dodanie autora do książki (relacja N:M).
     */
    @PostMapping("/books/{bookId}/authors")
    public String addAuthorToBook(@PathVariable Long bookId,
                                  @RequestParam Long authorId) throws Exception {
        bookService.addAuthor(bookId, authorId);
        return "redirect:/web/books/" + bookId;
    }

    /**
     * Usunięcie autora z książki (relacja N:M).
     */
    @PostMapping("/books/{bookId}/authors/{authorId}/remove")
    public String removeAuthorFromBook(@PathVariable Long bookId,
                                       @PathVariable Long authorId) throws Exception {
        bookService.removeAuthor(bookId, authorId);
        return "redirect:/web/books/" + bookId;
    }

    /**
     * Usuwanie książki.
     */
    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id) throws Exception {
        bookService.delete(id);
        return "redirect:/web/books";
    }


    // AUTHORS
    /**
     * Lista autorów: templates/authors/list.html
     */
    @GetMapping("/authors")
    public String authors(Model model) {
        model.addAttribute("authors", authorService.getAll());
        return "authors/list";
    }

    /**
     * Formularz dodawania autora: templates/authors/new.html
     */
    @GetMapping("/authors/new")
    public String newAuthor(Model model) {
        model.addAttribute("author", new CreateAuthorRequest());
        return "authors/new";
    }

    /**
     * Obsługa formularza dodawania autora.
     */
    @PostMapping("/authors")
    public String createAuthor(@Valid @ModelAttribute("author") CreateAuthorRequest author,
                               BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "authors/new";
        }

        authorService.create(author);
        return "redirect:/web/authors";
    }


    // LOANS
    /**
     * Lista wypożyczeń: templates/loans/list.html
     */
    @GetMapping("/loans")
    public String loans(Model model) {
        model.addAttribute("loans", loanService.getAll());
        return "loans/list";
    }

    /**
     * Formularz nowego wypożyczenia: templates/loans/new.html
     * Musimy przekazać listę userów i książek do selectów.
     */
    @GetMapping("/loans/new")
    public String newLoan(Model model) {
        model.addAttribute("loan", new CreateLoanRequest());
        model.addAttribute("users", userService.getAll());
        model.addAttribute("books", bookService.getAll());
        return "loans/new";
    }

    /**
     * Obsługa formularza nowego wypożyczenia.
     * Może rzucić:
     * - BusinessRuleException (np. brak dostępnych egzemplarzy)
     * - NotFoundException (nie ma usera/książki)
     * - ValidationException (np. dane usera błędne)
     */
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

    /**
     * Oddanie wypożyczenia (ustawia returnDate i zwiększa availableCopies książki).
     */
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

    /**
     * Usunięcie wypożyczenia.
     * Jeśli wypożyczenie nie było zwrócone, to dostępność książki zostaje przywrócona.
     */
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


    // USERS
    /**
     * Lista użytkowników: templates/users/list.html
     */
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users/list";
    }

    /**
     * Formularz dodawania użytkownika: templates/users/new.html
     */
    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("user", new CreateUserRequest());
        return "users/new";
    }

    /**
     * Obsługa formularza dodawania użytkownika.
     * ValidationException np. gdy email już istnieje.
     */
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

    /**
     * Szczegóły użytkownika: templates/users/details.html
     * Jeśli nie ma profilu -> tworzymy obiekt profile do formularza (relacja 1:1).
     */
    @GetMapping("/users/{id}")
    public String userDetails(@PathVariable Long id, Model model) throws Exception {
        var user = userService.getById(id);
        model.addAttribute("user", user);

        if (user.getProfile() == null) {
            model.addAttribute("profile", new CreateProfileRequest());
        }

        return "users/details";
    }

    /**
     * Usunięcie użytkownika.
     */
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) throws Exception {
        userService.delete(id);
        return "redirect:/web/users";
    }

    // PROFILE (1:1)
    /**
     * Utworzenie profilu dla użytkownika (tylko jeśli jeszcze nie istnieje).
     */
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

    /**
     * Usunięcie profilu użytkownika.
     */
    @PostMapping("/profiles/{profileId}/delete")
    public String deleteProfile(@PathVariable Long profileId) throws Exception {
        var profile = profileService.getById(profileId);
        Long userId = profile.getUser().getId();

        profileService.delete(profileId);
        return "redirect:/web/users/" + userId;
    }
}
