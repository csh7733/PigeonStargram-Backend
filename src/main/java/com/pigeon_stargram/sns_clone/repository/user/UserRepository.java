package com.pigeon_stargram.sns_clone.repository.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u " +
            "JOIN Follow f ON u.id = f.fromUser.id " +
            "WHERE u.id = :userId " +
            "AND (LOWER(f.toUser.name) LIKE LOWER(CONCAT('%', :key, '%')) " +
            "OR LOWER(f.toUser.location) LIKE LOWER(CONCAT('%', :key, '%')))")
    List<User> findFollowersByFilter(@Param("userId") Long userId, @Param("key") String key);

}
