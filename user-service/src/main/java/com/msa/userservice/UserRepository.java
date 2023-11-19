package com.msa.userservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select count(u) from User u where u.username = :username")
    long findByUsernameCount(@Param("username") String username);

    @Query("select u from User u join fetch u.roles where u.username = :username")
    Optional<User> findUserByUsernameFetch(@Param("username") String username);

    Optional<User> findUserByUsernameAndRefreshToken(String username, String refreshToken);

    Optional<User> findByUsername(String username);
}
