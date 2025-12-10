package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.Room_Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    
    @Query("SELECT r FROM Room r WHERE r.roomStatus.roomStatus = 'Available'")
    List<Room> findAvailableRooms();
    
    @Query("SELECT r FROM Room r WHERE r.roomType.roomTypeId = :roomTypeId AND r.roomStatus.roomStatus = 'Available'")
    List<Room> findAvailableRoomsByType(@Param("roomTypeId") int roomTypeId);
    
    List<Room> findByRoomStatus(Room_Status roomStatus);
}

