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

/**
 * Serwis odpowiedzialny za profile użytkownika (UserProfile).
 *
 * Relacja 1:1:
 * - jeden użytkownik może mieć maksymalnie jeden profil
 * - profil zawsze należy do konkretnego użytkownika
 */
@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;

    public ProfileService(ProfileRepository profileRepository, UserService userService) {
        this.profileRepository = profileRepository;
        this.userService = userService;
    }

    /**
     * Tworzy profil dla użytkownika.
     *
     * Użytkownik może mieć tylko jeden profil.
     * Dlatego przed utworzeniem sprawdzamy, czy profil już istnieje.
     */
    public UserProfile create(Long userId, CreateProfileRequest req)
            throws NotFoundException, BusinessRuleException {

        // pobieramy użytkownika (jak nie istnieje -> NotFoundException)
        User user = userService.getById(userId);

        // tylko 1 profil na użytkownika
        if (user.getProfile() != null) {
            throw new BusinessRuleException("Profile already exists for user: " + userId);
        }

        // tworzymy nową encję profilu i przypisujemy do użytkownika
        UserProfile profile = new UserProfile(req.getAddress(), req.getPhone(), user);
        user.setProfile(profile);

        // zapis przez Spring Data (ORM)
        return profileRepository.save(profile);
    }

    /**
     * Pobiera profil po ID.
     */
    @Transactional(readOnly = true)
    public UserProfile getById(Long profileId) throws NotFoundException {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found: " + profileId));
    }

    /**
     * Aktualizuje dane profilu.
     */
    public UserProfile update(Long profileId, UpdateProfileRequest req) throws NotFoundException {
        UserProfile profile = getById(profileId);

        profile.setAddress(req.getAddress());
        profile.setPhone(req.getPhone());

        return profileRepository.save(profile);
    }

    /**
     * Usuwa profil:
     * - odpinamy profil od użytkownika (user.profile = null),
     * - kasujemy rekord profilu z bazy.
     */
    public void delete(Long profileId) throws NotFoundException {
        UserProfile profile = getById(profileId);

        // odpinamy relację 1:1 po stronie User
        profile.getUser().setProfile(null);

        profileRepository.delete(profile);
    }
}
