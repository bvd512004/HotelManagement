package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.entity.Reservation;
import com.hsf302.hotelmanagement.repository.ReservationRepository;
import com.hsf302.hotelmanagement.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public List<Reservation> getReservationForThisMonth() {
        Date today = Date.from(LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        return reservationRepository.findReservationsThisMonthWithDetails(today);
    }

    @Override
    public List<Reservation> getReservationForToday() {
        Date today = Date.from(LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        return reservationRepository.findReservationsTodayWithDetails(today);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAllWithDetails();
    }
}
