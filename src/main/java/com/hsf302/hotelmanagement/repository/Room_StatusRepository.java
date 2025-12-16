package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Room_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Room_StatusRepository extends JpaRepository<Room_Status, Integer> {
}

