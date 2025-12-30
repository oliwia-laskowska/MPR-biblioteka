package pl.s32832.library.dto.response;

public record ProfileResponse(
        Long id,
        Long userId,
        String address,
        String phone
) {}
