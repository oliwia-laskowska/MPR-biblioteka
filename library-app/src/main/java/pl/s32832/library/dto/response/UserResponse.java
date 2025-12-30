package pl.s32832.library.dto.response;

public record UserResponse(
        Long id,
        String email,
        String fullName
) {}
