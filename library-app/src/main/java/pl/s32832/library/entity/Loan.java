package pl.s32832.library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Encja Loan = wypożyczenie książki przez użytkownika.
 *
 * Tabela: loans
 * Relacje:
 * - MANY-TO-ONE do User
 * - MANY-TO-ONE do Book
 */
@Entity
@Table(name = "loans")
public class Loan {

    /** Klucz główny wypożyczenia. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Użytkownik, który wypożyczył książkę.
     *
     * @ManyToOne => wiele wypożyczeń może należeć do jednego użytkownika
     * optional = false => user nie może być null (wymagane).
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Książka, która została wypożyczona.
     *
     * @ManyToOne => wiele wypożyczeń może dotyczyć jednej książki
     * optional = false => book nie może być null (wymagane).
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id")
    private Book book;

    /** Data wypożyczenia. */
    @Column(nullable = false)
    private LocalDate loanDate;

    /** Termin zwrotu. */
    @Column(nullable = false)
    private LocalDate dueDate;

    /**
     * Data zwrotu.
     * Jeśli null => wypożyczenie aktywne.
     */
    private LocalDate returnDate;

    public Loan() {

    }

    public Loan(User user, Book book, LocalDate loanDate, LocalDate dueDate) {
        this.user = user;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
}
