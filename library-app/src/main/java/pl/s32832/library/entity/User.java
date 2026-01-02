package pl.s32832.library.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Encja User = użytkownik biblioteki.
 *
 * Tabela: users
 * Relacje:
 * - 1:1 z UserProfile
 * - 1:N z Loan (wiele wypożyczeń jednego użytkownika)
 */
@Entity
@Table(name = "users")
public class User {

    /** Klucz główny użytkownika. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String fullName;

    /**
     * Relacja 1:1 — profil użytkownika.
     *
     * mappedBy="user" => właścicielem relacji jest pole "user" w encji UserProfile.
     * cascade=ALL => zapis/usunięcie usera zapisuje/usuwa też profil.
     * orphanRemoval=true => jeśli odłączysz profil od użytkownika, profil zostanie usunięty z bazy.
     * fetch=LAZY => profil nie jest pobierany automatycznie (dopiero przy użyciu getProfile()).
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserProfile profile;

    /**
     * Relacja 1:N — wypożyczenia użytkownika.
     *
     * mappedBy="user" => pole "user" w encji Loan wskazuje właściciela relacji.
     * cascade=ALL => zapis/usunięcie usera działa też na wypożyczenia.
     * orphanRemoval=true => usunięcie wypożyczenia z listy spowoduje usunięcie go z bazy.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

    public User() {
    }

    public User(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
    }

    public Long getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) { this.profile = profile; }

    public List<Loan> getLoans() { return loans; }
}
