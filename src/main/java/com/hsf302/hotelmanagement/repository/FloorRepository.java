package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorRepository extends JpaRepository<Floor,Integer> {
}
