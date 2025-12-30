package pl.s32832.library.mapper;

import pl.s32832.library.dto.response.ProfileResponse;
import pl.s32832.library.entity.UserProfile;

public class ProfileMapper {
    private ProfileMapper() {}

    public static ProfileResponse toResponse(UserProfile p) {
        return new ProfileResponse(p.getId(), p.getUser().getId(), p.getAddress(), p.getPhone());
    }
}
