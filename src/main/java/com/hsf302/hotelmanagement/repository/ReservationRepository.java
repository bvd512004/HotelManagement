package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByGuestId(int guestId);

    List<Reservation> findByStatus(String status);
    Page<Reservation> findByStatus(String status, Pageable pageable);

    // Tìm kiếm theo tên khách hàng hoặc email trong khoảng ngày check-in
    @Query("SELECT r FROM Reservation r WHERE r.status = :status AND r.checkInDate BETWEEN :startDate AND :endDate " +
            "AND (LOWER(r.guest.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.guest.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.guest.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Reservation> searchReservationsByGuestAndDateRange(
            @Param("searchTerm") String searchTerm,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("status") String status,
            Pageable pageable);

    // Tìm kiếm theo khoảng ngày check-in
    @Query("SELECT r FROM Reservation r WHERE r.status = :status AND r.checkInDate BETWEEN :startDate AND :endDate")
    Page<Reservation> findByStatusAndCheckInDateBetween(
            @Param("status") String status,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);

    // Tìm kiếm theo status và tên khách
    @Query("SELECT r FROM Reservation r WHERE r.status = :status AND (LOWER(r.guest.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(r.guest.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Reservation> findByStatusAndGuestFullNameContainingIgnoreCase(
            @Param("status") String status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    @Query("SELECT r FROM Reservation r JOIN r.reservation_rooms rr WHERE rr.room.roomId = :roomId")
    List<Reservation> findByRoomId(@Param("roomId") int roomId);
}
