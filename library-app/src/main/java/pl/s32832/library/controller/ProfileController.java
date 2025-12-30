package pl.s32832.library.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.s32832.library.dto.request.CreateProfileRequest;
import pl.s32832.library.dto.request.UpdateProfileRequest;
import pl.s32832.library.dto.response.ProfileResponse;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.mapper.ProfileMapper;
import pl.s32832.library.service.ProfileService;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/users/{userId}/profile")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse create(@PathVariable("userId") Long userId,
                                  @Valid @RequestBody CreateProfileRequest req)
            throws NotFoundException, BusinessRuleException {
        return ProfileMapper.toResponse(profileService.create(userId, req));
    }


    @GetMapping("/profiles/{profileId}")
    public ProfileResponse get(@PathVariable Long profileId) throws NotFoundException {
        return ProfileMapper.toResponse(profileService.getById(profileId));
    }

    @PutMapping("/profiles/{profileId}")
    public ProfileResponse update(@PathVariable Long profileId, @Valid @RequestBody UpdateProfileRequest req)
            throws NotFoundException {
        return ProfileMapper.toResponse(profileService.update(profileId, req));
    }

    @DeleteMapping("/profiles/{profileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long profileId) throws NotFoundException {
        profileService.delete(profileId);
    }
}
