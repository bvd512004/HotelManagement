package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Reservation_Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationServiceRepository extends JpaRepository<Reservation_Service, Integer> {


}
