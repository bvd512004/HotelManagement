package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Reservation;
import java.util.List;
import com.hsf302.hotelmanagement.entity.*;
import java.util.Date;

public interface ReservationService {
    Reservation save(Reservation reservation);
    Reservation findById(int id);
    List<Reservation> findAll();
    void delete(int id);
    List<Reservation> findByGuestId(int guestId);
    List<Service> getAllServices();
    List<Reservation> findConflictingReservations(int roomId, Date checkIn, Date checkOut);
    List<Reservation> getReservationForToday();
    List<Reservation> getReservationForThisMonth();
    List<Reservation> getAllReservations();
}
