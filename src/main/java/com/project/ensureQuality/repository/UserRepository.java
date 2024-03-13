package com.project.ensureQuality.repository;

import com.project.ensureQuality.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Boolean existsByPhoneNumber(String phoneNumber);

    Boolean existsByEmail(String email);

    @Query("select u from User u order by u.id asc limit ?2 offset ?1")
    List<User> getUserWithPagination(int offset, int limit);

    @Query("select u from User u where u.email = ?1 or u.phoneNumber = ?1")
    Optional<User> findByEmailOrPhoneNumber(String valueLogin);

}
