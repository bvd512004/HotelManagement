package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Reservation;
import com.hsf302.hotelmanagement.entity.Reservation_Service;
import com.hsf302.hotelmanagement.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationServiceRepository extends JpaRepository<Reservation_Service, Integer> {
    Optional<Reservation_Service> findByReservationAndService(Reservation reservation, Service service);
    void deleteByReservationAndService(Reservation reservation, Service service);
}

