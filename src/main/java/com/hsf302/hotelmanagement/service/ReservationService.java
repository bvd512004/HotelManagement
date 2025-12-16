package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Reservation;

import java.util.List;

public interface ReservationService {
    List<Reservation> getReservationForToday();
    List<Reservation> getReservationForThisMonth();
    List<Reservation> getAllReservations();
}
