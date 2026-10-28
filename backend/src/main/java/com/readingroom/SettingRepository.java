package com.readingroom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SettingRepository extends JpaRepository<LibrarySetting,Integer> {
 Optional<LibrarySetting> findByKey(String key);
}
