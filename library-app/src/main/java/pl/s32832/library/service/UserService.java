package pl.s32832.library.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.s32832.library.dto.request.CreateUserRequest;
import pl.s32832.library.dto.request.UpdateUserRequest;
import pl.s32832.library.entity.User;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.repository.UserRepository;

import java.util.List;

/**
 * Serwis odpowiedzialny za użytkowników (User).
 *
 * Realizuje logikę biznesową oraz dostęp do danych poprzez UserRepository.
 * Użytkownik jest też powiązany z:
 * - profilem (1:1)
 * - wypożyczeniami (1:N)
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Tworzy nowego użytkownika.
     *
     * Walidacja:
     * - email musi być unikalny (sprawdzamy w bazie).
     */
    public User create(CreateUserRequest req) throws ValidationException {

        // sprawdzenie unikalności emaila
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists: " + req.getEmail());
        }

        User user = new User(req.getEmail(), req.getFullName());

        // zapis do bazy przez Spring Data JPA (ORM)
        return userRepository.save(user);
    }

    /**
     * Pobiera użytkownika po ID.
     * Jeśli nie istnieje -> NotFoundException.
     */
    @Transactional(readOnly = true)
    public User getById(Long id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    /**
     * Pobiera wszystkich użytkowników.
     */
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * Aktualizuje dane użytkownika.
     * W tym projekcie edytujemy tylko fullName.
     */
    public User update(Long id, UpdateUserRequest req) throws NotFoundException {
        User user = getById(id);

        user.setFullName(req.getFullName());

        return userRepository.save(user);
    }

    /**
     * Usuwa użytkownika.
     */
    public void delete(Long id) throws NotFoundException {
        User user = getById(id);
        userRepository.delete(user);
    }
}
