package pl.s32832.library.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Encja Author = autor książek.
 *
 * Tabela: authors
 * Relacja:
 * - MANY-TO-MANY z Book
 */
@Entity
@Table(name = "authors")
public class Author {

    /** Klucz główny autora. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * Książki tego autora.
     *
     * mappedBy = "authors" oznacza, że tabela pośrednia
     * jest zdefiniowana w encji Book, a Author jest stroną „odwrotną”.
     *
     * HashSet, żeby uniknąć duplikatów i mieć stabilne zachowanie.
     */
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Set<Book> getBooks() { return books; }
}
