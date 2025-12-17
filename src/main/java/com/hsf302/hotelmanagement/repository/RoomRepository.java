package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.Room_Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    
    @Query("SELECT r FROM Room r WHERE r.roomStatus.roomStatus = 'Available'")
    List<Room> findAvailableRooms();
    
    @Query("SELECT r FROM Room r WHERE r.roomType.roomTypeId = :roomTypeId AND r.roomStatus.roomStatus = 'Available'")
    List<Room> findAvailableRoomsByType(@Param("roomTypeId") int roomTypeId);

    // Tìm phòng theo trạng thái
    List<Room> findByRoomStatus(Room_Status roomStatus);

    @Query("""
        SELECT r FROM Room r
        WHERE r.roomType.roomTypeId = :roomTypeId
          AND r.roomStatus.roomStatus = 'Available'
          AND r.roomId NOT IN (
              SELECT rr.room.roomId FROM Reservation_Room rr
              JOIN rr.reservation res
              WHERE res.checkInDate < :checkOutDate
                AND res.checkOutDate > :checkInDate
                AND (res.status IS NULL OR res.status <> 'Cancelled')
          )
        """)
    List<Room> findAvailableRoomsByTypeAndDate(@Param("roomTypeId") int roomTypeId,
                                               @Param("checkInDate") Date checkInDate,
                                               @Param("checkOutDate") Date checkOutDate);

    @Query("SELECT count(r) FROM Room r WHERE r.roomType.roomTypeId = :roomTypeId")
    int countByRoomTypeId(@Param("roomTypeId") int roomTypeId);

    @Query("""
        SELECT r FROM Room r
        WHERE r.roomStatus.roomStatus = 'Available'
          AND (:roomTypeId IS NULL OR r.roomType.roomTypeId = :roomTypeId)
          AND (:floorId IS NULL OR r.floor.floorId = :floorId)
        """)
    Page<Room> findAvailableRoomsFiltered(@Param("roomTypeId") Integer roomTypeId,
                                          @Param("floorId") Integer floorId,
                                          Pageable pageable);

    @Query("""
        SELECT r FROM Room r
        WHERE r.roomStatus.roomStatus = 'Available'
          AND (:roomTypeId IS NULL OR r.roomType.roomTypeId = :roomTypeId)
          AND (:floorId IS NULL OR r.floor.floorId = :floorId)
          AND r.roomId NOT IN (
              SELECT rr.room.roomId FROM Reservation_Room rr
              JOIN rr.reservation res
              WHERE res.checkInDate < :checkOutDate
                AND res.checkOutDate > :checkInDate
                AND (res.status IS NULL OR res.status <> 'Cancelled')
          )
        """)
    Page<Room> findAvailableRoomsFilteredByDate(@Param("roomTypeId") Integer roomTypeId,
                                                 @Param("floorId") Integer floorId,
                                                 @Param("checkInDate") Date checkInDate,
                                                 @Param("checkOutDate") Date checkOutDate,
                                                 Pageable pageable);

    @Query("SELECT r FROM Room r JOIN FETCH r.roomType JOIN FETCH r.roomStatus JOIN FETCH r.floor")
    List<Room> findAllWithRelations();
}
