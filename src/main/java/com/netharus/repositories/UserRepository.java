package com.netharus.repositories;

import com.netharus.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u from User u where cast(u.id as string) like lower(concat('%',:keyword,'%')) " +
            "or lower(u.username) like lower(concat('%',:keyword,'%'))")
    Page<User> findAll(Pageable pageable, @Param("keyword") String keyword);
}
