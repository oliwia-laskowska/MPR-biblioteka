package pl.s32832.library.entity;

import jakarta.persistence.*;

import java.util.*;

/**
 * Encja Book = książka w bibliotece.
 *
 * Tabela: books
 * Relacje:
 * - MANY-TO-MANY z Author (przez tabelę pośrednią book_authors)
 * - ONE-TO-MANY z Loan
 */
@Entity
@Table(name = "books")
public class Book {

    /** Klucz główny książki. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String isbn;

    /** Łączna liczba egzemplarzy w bibliotece. */
    @Column(nullable = false)
    private int totalCopies;

    /**
     * Liczba dostępnych egzemplarzy (zmniejsza się przy wypożyczeniu, zwiększa się przy zwrocie).
     */
    @Column(nullable = false)
    private int availableCopies;

    /**
     * Autorzy przypisani do książki.
     *
     * Relacja MANY-TO-MANY realizowana tabelą pośrednią "book_authors".
     */
    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    /**
     * Wypożyczenia danej książki historia.
     */
    @OneToMany(mappedBy = "book")
    private List<Loan> loans = new ArrayList<>();

    public Book() {

    }

    /**
     * Konstruktor używany np. przy tworzeniu książki w serwisie.
     * Ustawia availableCopies = totalCopies na start.
     */
    public Book(String title, String isbn, int totalCopies) {
        this.title = title;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public Set<Author> getAuthors() { return authors; }

    public List<Loan> getLoans() { return loans; }
}
