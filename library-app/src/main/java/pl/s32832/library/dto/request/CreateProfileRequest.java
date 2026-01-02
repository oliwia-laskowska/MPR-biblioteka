package pl.s32832.library.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO do utworzenia profilu użytkownika (relacja 1:1).
 * Używane w:
 * - REST: POST /api/users/{userId}/profile
 * - GUI (Thymeleaf): formularz w szczegółach użytkownika (users/details.html)
 *
 * Profil przechowuje dodatkowe dane użytkownika,
 * które nie są częścią encji User (np. adres, telefon).
 */
public class CreateProfileRequest {

    @NotBlank
    private String address;


    @NotBlank
    private String phone;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
