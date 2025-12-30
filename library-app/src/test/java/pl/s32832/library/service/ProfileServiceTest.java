package pl.s32832.library.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.s32832.library.dto.request.CreateProfileRequest;
import pl.s32832.library.dto.request.UpdateProfileRequest;
import pl.s32832.library.entity.User;
import pl.s32832.library.entity.UserProfile;
import pl.s32832.library.exception.BusinessRuleException;
import pl.s32832.library.exception.NotFoundException;
import pl.s32832.library.repository.ProfileRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    ProfileRepository profileRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ProfileService profileService;

    @Test
    void create_shouldThrowIfProfileExists() throws Exception {
        User u = new User("a@b.com", "X");
        u.setProfile(new UserProfile("addr", "123", u));

        when(userService.getById(1L)).thenReturn(u);

        CreateProfileRequest req = new CreateProfileRequest();
        req.setAddress("A");
        req.setPhone("P");

        assertThrows(BusinessRuleException.class, () -> profileService.create(1L, req));
        verify(profileRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowWhenUserNotFound() throws Exception {
        when(userService.getById(1L)).thenThrow(new NotFoundException("x"));

        CreateProfileRequest req = new CreateProfileRequest();
        req.setAddress("A");
        req.setPhone("P");

        assertThrows(NotFoundException.class, () -> profileService.create(1L, req));
    }

    @Test
    void update_shouldUpdateProfile() throws Exception {
        User u = new User("a@b.com", "X");
        UserProfile p = new UserProfile("old", "111", u);
        u.setProfile(p);

        when(profileRepository.findById(1L)).thenReturn(Optional.of(p));
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setAddress("new");
        req.setPhone("999");

        UserProfile updated = profileService.update(1L, req);

        assertEquals("new", updated.getAddress());
        assertEquals("999", updated.getPhone());
    }

    @Test
    void delete_shouldRemoveProfileFromUser() throws Exception {
        User u = new User("a@b.com", "X");
        UserProfile p = new UserProfile("addr", "111", u);
        u.setProfile(p);

        when(profileRepository.findById(1L)).thenReturn(Optional.of(p));

        profileService.delete(1L);

        assertNull(u.getProfile());
        verify(profileRepository).delete(p);
    }
}
