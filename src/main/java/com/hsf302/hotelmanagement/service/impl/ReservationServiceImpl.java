package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.entity.Reservation;
import com.hsf302.hotelmanagement.entity.Reservation_Room;
import com.hsf302.hotelmanagement.entity.Reservation_Service;
import com.hsf302.hotelmanagement.entity.Service;
import com.hsf302.hotelmanagement.repository.ReservationServiceRepository;
import com.hsf302.hotelmanagement.repository.ReservationRepository;
import com.hsf302.hotelmanagement.repository.ServiceRepository;
import com.hsf302.hotelmanagement.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.hsf302.hotelmanagement.repository.ServiceRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ReservationServiceRepository reservationServiceRepository;



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
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public com.hsf302.hotelmanagement.entity.Service getServiceById(int id) {
        return serviceRepository.findById(id).orElse(null);
    }

    @Override
    public List<Reservation> findConflictingReservations(int roomId, Date checkIn, Date checkOut) {
        List<Reservation> allReservations = reservationRepository.findAll();
        return allReservations.stream()
                .filter(res -> res.getReservation_rooms() != null &&
                        res.getReservation_rooms().stream()
                                .anyMatch(rr -> rr.getRoom().getRoomId() == roomId))
                .filter(res -> !res.getStatus().equals("Cancelled"))
                .filter(res -> checkIn.before(res.getCheckOutDate()) && checkOut.after(res.getCheckInDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> getReservationForThisMonth() {
        Date today = Date.from(LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        return reservationRepository.findReservationsThisMonthWithDetails(today);
    }

    @Override
    public List<Reservation> getReservationForToday() {
        LocalDate todayLocalDate = LocalDate.now();
        Date startOfToday = Date.from(todayLocalDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        LocalDate tomorrowLocalDate = todayLocalDate.plusDays(1);
        Date startOfTomorrow = Date.from(tomorrowLocalDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        return reservationRepository.findReservationsTodayWithDetails(startOfToday, startOfTomorrow);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAllWithDetails();
    }

    @Override
    public double calculateTotalRoomAmount(String filter) {
        List<Reservation> reservations = getFilteredReservations(filter);
        double total = 0.0;
        for (Reservation res : reservations) {
            for (Reservation_Room reservationRoom : res.getReservation_rooms()) {
                double basePrice = reservationRoom.getRoom().getRoomType().getBasePrice();
                LocalDate in = res.getCheckInDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate out = res.getCheckOutDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                long numberOfDays = ChronoUnit.DAYS.between(in, out);
                if (numberOfDays <= 0) numberOfDays = 1;
                total += basePrice * numberOfDays;
            }
        }
        return total;
    }

    @Override
    public double calculateTotalServiceAmount(String filter) {
        List<Reservation> reservations = getFilteredReservations(filter);
        List<Service> services = serviceRepository.findAll();
        double total = 0.0;
        List<Reservation_Service> resList = reservationServiceRepository.findAll();
        for(Reservation_Service res : resList){
            for(Service service : services){
                if(res.getService().getServiceId() == service.getServiceId()){
                    for(Reservation res2 : reservations){
                        if(res.getReservation().getReservationId() == res2.getReservationId()){
                            total += res.getQuantity() * service.getPrice();
                        }
                    }
                }
            }
        }


        return total;
    }
    @Override
    public List<Reservation> findByRoomId(int roomId) {
        return reservationRepository.findByRoomId(roomId);
    }

    @Override
    public double calculateTotalIncome(String filter) {
        double roomAmount = calculateTotalRoomAmount(filter);
        double serviceAmount = calculateTotalServiceAmount(filter);
        return roomAmount + serviceAmount;
    }

    private List<Reservation> getFilteredReservations(String filter) {
        if ("today".equals(filter)) {
            return getReservationForToday();
        } else if ("month".equals(filter)) {
            return getReservationForThisMonth();
        } else {
            return getAllReservations();
        }
    }
}
