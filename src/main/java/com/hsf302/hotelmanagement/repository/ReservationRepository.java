package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.guest " +
            "JOIN FETCH r.reservation_rooms rr " +
            "JOIN FETCH rr.room room " +
            "JOIN FETCH room.roomType " +
            "WHERE r.checkInDate = :date")
    List<Reservation> findReservationsTodayWithDetails(@Param("date") Date date);

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.guest " +
            "JOIN FETCH r.reservation_rooms rr " +
            "JOIN FETCH rr.room room " +
            "JOIN FETCH room.roomType " +
            "WHERE FUNCTION('YEAR', r.checkInDate) = FUNCTION('YEAR', :date) AND FUNCTION('MONTH', r.checkInDate) = FUNCTION('MONTH', :date)")
    List<Reservation> findReservationsThisMonthWithDetails(@Param("date") Date date);

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.guest " +
            "JOIN FETCH r.reservation_rooms rr " +
            "JOIN FETCH rr.room room " +
            "JOIN FETCH room.roomType")
    List<Reservation> findAllWithDetails();
}
