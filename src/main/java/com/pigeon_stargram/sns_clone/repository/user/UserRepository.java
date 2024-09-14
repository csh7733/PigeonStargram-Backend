package com.pigeon_stargram.sns_clone.repository.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByWorkEmail(String email);
    Optional<User> findByWorkEmailAndPassword(String email, String password);
    Optional<User> findByName(String name);
    List<User> findByNameContainingIgnoreCase(String searchQuery);

}
