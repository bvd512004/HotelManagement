package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService {
    List<User> findAll();
    List<User> searchUsers(String name, String role);
    Page<User> searchUsersPage(String name, String role, Pageable pageable);
    User findById(int id);
    User findByUserName(String userName);
    void save(User user);
    void update(User user);
    void deleteById(int id);
}
