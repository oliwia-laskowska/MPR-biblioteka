package pl.s32832.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.s32832.library.entity.UserProfile;

public interface ProfileRepository extends JpaRepository<UserProfile, Long> {
}
