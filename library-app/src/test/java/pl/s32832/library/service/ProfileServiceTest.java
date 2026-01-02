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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    ProfileRepository profileRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ProfileService profileService;

    // Nie można stworzyć profilu jeśli user już go ma
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

    // Gdy user nie istnieje -> NotFoundException
    @Test
    void create_shouldThrowWhenUserNotFound() throws Exception {
        when(userService.getById(1L)).thenThrow(new NotFoundException("User not found"));

        CreateProfileRequest req = new CreateProfileRequest();
        req.setAddress("A");
        req.setPhone("P");

        assertThrows(NotFoundException.class, () -> profileService.create(1L, req));

        verify(profileRepository, never()).save(any());
    }

    // Tworzenie profilu dla usera bez profilu
    @Test
    void create_shouldCreateProfile() throws Exception {
        User u = new User("a@b.com", "X");

        when(userService.getById(1L)).thenReturn(u);
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateProfileRequest req = new CreateProfileRequest();
        req.setAddress("Warszawa");
        req.setPhone("123456789");

        UserProfile created = profileService.create(1L, req);

        assertNotNull(created);
        assertEquals("Warszawa", created.getAddress());
        assertEquals("123456789", created.getPhone());
        assertEquals(u, created.getUser());
        assertNotNull(u.getProfile());
        assertEquals("Warszawa", u.getProfile().getAddress());

        verify(profileRepository).save(any(UserProfile.class));
    }

    // Update: zmienia address + phone
    @Test
    void update_shouldUpdateProfile() throws Exception {
        User u = new User("a@b.com", "X");
        UserProfile p = new UserProfile("old", "111", u);

        when(profileRepository.findById(1L)).thenReturn(Optional.of(p));
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setAddress("new");
        req.setPhone("999");

        UserProfile updated = profileService.update(1L, req);

        assertEquals("new", updated.getAddress());
        assertEquals("999", updated.getPhone());

        verify(profileRepository).save(p);
    }

    // Update: profil nie istnieje -> NotFoundException
    @Test
    void update_shouldThrowWhenProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setAddress("new");
        req.setPhone("999");

        assertThrows(NotFoundException.class, () -> profileService.update(1L, req));
        verify(profileRepository, never()).save(any());
    }

    // Delete: usuwa profil i odłącza go od usera (relacja 1:1)
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

    // Delete: profil nie istnieje -> NotFoundException
    @Test
    void delete_shouldThrowWhenProfileNotFound() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> profileService.delete(1L));
        verify(profileRepository, never()).delete(any());
    }
}
