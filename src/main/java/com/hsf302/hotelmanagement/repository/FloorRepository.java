package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Integer> {
    @Query("SELECT f FROM Floor f WHERE f.floorNumber = :floorNumber")
    Optional<Floor> findFirstByFloorNumber(@Param("floorNumber") int floorNumber);
}
