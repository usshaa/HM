package com.hotelmanagement.repository;

import com.hotelmanagement.entity.User;
import com.hotelmanagement.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Page<User> findByRole(UserRole role, Pageable pageable);
    Page<User> findByRoleAndIsActiveTrue(UserRole role, Pageable pageable);
}
