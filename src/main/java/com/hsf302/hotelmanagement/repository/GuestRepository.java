package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Integer> {
    Optional<Guest> findByEmail(String email);
    Optional<Guest> findByPhoneNumber(String phoneNumber);
}

