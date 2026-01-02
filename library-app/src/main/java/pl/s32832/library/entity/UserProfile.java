package pl.s32832.library.entity;

import jakarta.persistence.*;

/**
 * Encja UserProfile = dane dodatkowe użytkownika.
 *
 * Tabela: user_profiles
 * Relacja: 1:1 z User
 */
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    /** Klucz główny profilu. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    /**
     * Relacja 1:1 — profil jest przypisany do jednego użytkownika.
     *
     * @JoinColumn(name="user_id") => w tabeli user_profiles będzie kolumna user_id (FK do users.id)
     * nullable=false => profil MUSI mieć usera
     * unique=true => dany user może mieć maksymalnie jeden profil (wymuszone w bazie)
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public UserProfile() {
    }

    public UserProfile(String address, String phone, User user) {
        this.address = address;
        this.phone = phone;
        this.user = user;
    }

    public Long getId() { return id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
