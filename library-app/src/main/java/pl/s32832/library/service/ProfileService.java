package pl.s32832.library.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.s32832.library.dto.request.CreateProfileRequest;
import pl.s32832.library.dto.request.UpdateProfileRequest;
import pl.s32832.library.entity.User;
import pl.s32832.library.entity.UserProfile;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.repository.ProfileRepository;

@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;

    public ProfileService(ProfileRepository profileRepository, UserService userService) {
        this.profileRepository = profileRepository;
        this.userService = userService;
    }

    public UserProfile create(Long userId, CreateProfileRequest req) throws NotFoundException, BusinessRuleException {
        User user = userService.getById(userId);
        if (user.getProfile() != null) {
            throw new BusinessRuleException("Profile already exists for user: " + userId);
        }
        UserProfile profile = new UserProfile(req.getAddress(), req.getPhone(), user);
        user.setProfile(profile);
        return profileRepository.save(profile);
    }

    @Transactional(readOnly = true)
    public UserProfile getById(Long profileId) throws NotFoundException {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found: " + profileId));
    }

    public UserProfile update(Long profileId, UpdateProfileRequest req) throws NotFoundException {
        UserProfile profile = getById(profileId);
        profile.setAddress(req.getAddress());
        profile.setPhone(req.getPhone());
        return profileRepository.save(profile);
    }

    public void delete(Long profileId) throws NotFoundException {
        UserProfile profile = getById(profileId);
        profile.getUser().setProfile(null);
        profileRepository.delete(profile);
    }
}
