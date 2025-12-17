package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserName(String userName);
    
    List<User> findByRole(String role);

    List<User> findByUserNameContainingIgnoreCase(String userName);

    List<User> findByUserNameContainingIgnoreCaseAndRole(String userName, String role);

    // Pageable methods
    Page<User> findByRole(String role, Pageable pageable);

    Page<User> findByUserNameContainingIgnoreCase(String userName, Pageable pageable);

    Page<User> findByUserNameContainingIgnoreCaseAndRole(String userName, String role, Pageable pageable);
}
