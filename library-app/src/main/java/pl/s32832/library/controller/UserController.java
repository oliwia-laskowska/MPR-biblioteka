package pl.s32832.library.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.s32832.library.dto.request.CreateUserRequest;
import pl.s32832.library.dto.request.UpdateUserRequest;
import pl.s32832.library.dto.response.UserResponse;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.exception.ValidationException;
import pl.s32832.library.mapper.UserMapper;
import pl.s32832.library.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest req) throws ValidationException {
        return UserMapper.toResponse(userService.create(req));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) throws NotFoundException {
        return UserMapper.toResponse(userService.getById(id));
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll().stream().map(UserMapper::toResponse).toList();
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) throws NotFoundException {
        return UserMapper.toResponse(userService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws NotFoundException {
        userService.delete(id);
    }
}
