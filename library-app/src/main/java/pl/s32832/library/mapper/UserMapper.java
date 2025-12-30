package pl.s32832.library.mapper;

import pl.s32832.library.dto.response.UserResponse;
import pl.s32832.library.entity.User;

public class UserMapper {
    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName());
    }
}
