package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Reservation_Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRoomRepository extends JpaRepository<Reservation_Room, Integer> {

    @Query("SELECT rr FROM Reservation_Room rr " +
           "JOIN rr.reservation r " +
           "WHERE rr.room.roomId = :roomId " +
           "AND r.checkInDate < :endDateTime " +
           "AND r.checkOutDate > :startDateTime")
    List<Reservation_Room> findReservationByRoomAndDateRange(
            @Param("roomId") int roomId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
}