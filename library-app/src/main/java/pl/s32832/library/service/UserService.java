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

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(CreateUserRequest req) throws ValidationException {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists: " + req.getEmail());
        }
        User user = new User(req.getEmail(), req.getFullName());
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User update(Long id, UpdateUserRequest req) throws NotFoundException {
        User user = getById(id);
        user.setFullName(req.getFullName());
        return userRepository.save(user);
    }

    public void delete(Long id) throws NotFoundException {
        User user = getById(id);
        userRepository.delete(user);
    }
}
