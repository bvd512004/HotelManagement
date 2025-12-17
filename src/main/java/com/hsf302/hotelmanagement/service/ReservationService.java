package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.*;
import java.util.Date;
import java.util.List;

public interface ReservationService {
    Reservation save(Reservation reservation);
    Reservation findById(int id);
    List<Reservation> findAll();
    void delete(int id);
    List<Reservation> findByGuestId(int guestId);
    List<Service> getAllServices();
    Service getServiceById(int id);
    List<Reservation> findConflictingReservations(int roomId, Date checkIn, Date checkOut);
    List<Reservation> findByRoomId(int roomId);
    List<Reservation> getReservationForThisMonth();
    List<Reservation> getReservationForToday();
    List<Reservation> getAllReservations();

    double calculateTotalRoomAmount(String filter);
    double calculateTotalServiceAmount(String filter);
    double calculateTotalIncome(String filter);
}