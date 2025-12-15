package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Reservation_Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRoomRepository extends JpaRepository<Reservation_Room, Integer> {
}

