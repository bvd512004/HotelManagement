package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.entity.User;
import com.hsf302.hotelmanagement.repository.UserRepository;
import com.hsf302.hotelmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> searchUsers(String name, String role) {
        boolean hasName = StringUtils.hasText(name);
        boolean hasRole = StringUtils.hasText(role);

        if (hasName && hasRole) {
            return userRepository.findByUserNameContainingIgnoreCaseAndRole(name, role);
        } else if (hasName) {
            return userRepository.findByUserNameContainingIgnoreCase(name);
        } else if (hasRole) {
            return userRepository.findByRole(role);
        } else {
            return userRepository.findAll();
        }
    }

    @Override
    public Page<User> searchUsersPage(String name, String role, Pageable pageable) {
        boolean hasName = StringUtils.hasText(name);
        boolean hasRole = StringUtils.hasText(role);

        if (hasName && hasRole) {
            return userRepository.findByUserNameContainingIgnoreCaseAndRole(name, role, pageable);
        } else if (hasName) {
            return userRepository.findByUserNameContainingIgnoreCase(name, pageable);
        } else if (hasRole) {
            return userRepository.findByRole(role, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    @Transactional
    public void save(User user) {
        // In a real app, you'd encode the password here before saving
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(User userForm) {
        User existingUser = findById(userForm.getUserId());
        if (existingUser != null) {
            existingUser.setUserName(userForm.getUserName());
            existingUser.setRole(userForm.getRole());

            // Only update the password if a new one is provided
            if (userForm.getPassword() != null && !userForm.getPassword().isEmpty()) {
                // You should encode the new password here
                existingUser.setPassword(userForm.getPassword());
            }
            // If the password field is empty, we do nothing, preserving the old password

            userRepository.save(existingUser);
        }
    }

    @Override
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }
}
