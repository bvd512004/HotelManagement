package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.entity.Reservation;
import com.hsf302.hotelmanagement.repository.ReservationRepository;
import com.hsf302.hotelmanagement.repository.ServiceRepository;
import com.hsf302.hotelmanagement.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Override
    @Transactional
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation findById(int id) {
        return reservationRepository.findById(id).orElse(null);
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(int id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public List<Reservation> findByGuestId(int guestId) {
        return reservationRepository.findByGuestId(guestId);
    }

    @Override
    public List<com.hsf302.hotelmanagement.entity.Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public List<Reservation> findConflictingReservations(int roomId, Date checkIn, Date checkOut) {
        // Lấy tất cả reservation
        List<Reservation> allReservations = reservationRepository.findAll();

        // Lọc các reservation có phòng trùng và thời gian trùng
        return allReservations.stream()
                .filter(res -> res.getReservation_rooms() != null &&
                        res.getReservation_rooms().stream()
                                .anyMatch(rr -> rr.getRoom().getRoomId() == roomId))
                .filter(res -> !res.getStatus().equals("Cancelled")) // Bỏ qua các reservation bị hủy
                .filter(res -> {
                    // Kiểm tra xem khoảng thời gian có trùng không
                    // Conflict nếu: checkIn < resCheckOut AND checkOut > resCheckIn
                    return checkIn.before(res.getCheckOutDate()) &&
                           checkOut.after(res.getCheckInDate());
                })
                .collect(Collectors.toList());
    }
}

