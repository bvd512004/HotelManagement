package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Reservation;
import com.hsf302.hotelmanagement.entity.Reservation_Room;
import com.hsf302.hotelmanagement.entity.Reservation_Service;
import com.hsf302.hotelmanagement.entity.Service;
import com.hsf302.hotelmanagement.exception.BookingException;
import com.hsf302.hotelmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ReceptionistService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ReservationServiceRepository reservationServiceRepository;

    @Autowired
    private ReservationRoomRepository reservationRoomRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomStatusRepository roomStatusRepository;

    public Page<Reservation> getReservationsByDateRange(
            String searchTerm,
            String fromDate,
            String toDate,
            String status,
            Pageable pageable) {

        Date startDate = fromDate != null && !fromDate.isEmpty() ?
            java.sql.Date.valueOf(fromDate) :
            getStartOfToday();

        Date endDate = toDate != null && !toDate.isEmpty() ?
            java.sql.Date.valueOf(toDate) :
            getEndOfToday();

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return reservationRepository.findByStatusAndCheckInDateBetween(status, startDate, endDate, pageable);
        } else {
            return reservationRepository.searchReservationsByGuestAndDateRange(
                searchTerm.trim(), startDate, endDate, status, pageable);
        }
    }

    private Date getStartOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    private Date getEndOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    public Page<Reservation> getCheckInReservations(String searchTerm, String dateRange, Pageable pageable) {
        Date[] dateRange_obj = getDateRange(dateRange);
        Date startDate = dateRange_obj[0];
        Date endDate = dateRange_obj[1];

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return reservationRepository.findByStatusAndCheckInDateBetween("Pending", startDate, endDate, pageable);
        } else {
            return reservationRepository.searchReservationsByGuestAndDateRange(
                    searchTerm.trim(), startDate, endDate, "Pending", pageable);
        }
    }

    private Date[] getDateRange(String dateRange) {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();

        if (dateRange == null || dateRange.equals("today")) {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        } else if (dateRange.equals("week")) {
            cal.add(Calendar.DAY_OF_MONTH, 7);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        } else if (dateRange.equals("month")) {
            cal.add(Calendar.DAY_OF_MONTH, 30);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        }

        Date endDate = cal.getTime();
        return new Date[]{startDate, endDate};
    }

    @Transactional
    public Reservation checkInReservation(Integer reservationId, LocalDateTime checkInDateTime) throws BookingException {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);

        if (!reservation.isPresent()) {
            throw new BookingException("Không tìm thấy đặt phòng với ID: " + reservationId);
        }

        Reservation res = reservation.get();
        res.setStatus("Confirmed");

        List<Reservation_Room> rooms = res.getReservation_rooms();
        if (rooms != null && !rooms.isEmpty()) {
            // Lấy Room_Status OCCUPIED từ database
            Optional<com.hsf302.hotelmanagement.entity.Room_Status> occupiedStatus =
                roomStatusRepository.findByRoomStatus("OCCUPIED");

            if (occupiedStatus.isPresent()) {
                for (Reservation_Room resRoom : rooms) {
                    resRoom.setStatus("CheckedIn");
                    reservationRoomRepository.save(resRoom);

                    com.hsf302.hotelmanagement.entity.Room room = resRoom.getRoom();
                    if (room != null) {
                        room.setRoomStatus(occupiedStatus.get());
                        roomRepository.save(room);
                    }
                }
            } else {
                throw new BookingException("Không tìm thấy trạng thái phòng OCCUPIED trong hệ thống");
            }
        }

        return reservationRepository.save(res);
    }

    @Transactional
    public Reservation checkOutReservation(Integer reservationId) throws BookingException {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);

        if (!reservation.isPresent()) {
            throw new BookingException("Không tìm thấy đặt phòng với ID: " + reservationId);
        }

        Reservation res = reservation.get();
        res.setStatus("CheckedOut");

        List<Reservation_Room> rooms = res.getReservation_rooms();
        if (rooms != null && !rooms.isEmpty()) {
            Optional<com.hsf302.hotelmanagement.entity.Room_Status> dirtyStatus =
                roomStatusRepository.findByRoomStatus("DIRTY");

            if (dirtyStatus.isPresent()) {
                for (Reservation_Room resRoom : rooms) {
                    resRoom.setStatus("CheckedOut");
                    reservationRoomRepository.save(resRoom);

                    com.hsf302.hotelmanagement.entity.Room room = resRoom.getRoom();
                    if (room != null) {
                        room.setRoomStatus(dirtyStatus.get());
                        roomRepository.save(room);
                    }
                }
            } else {
                throw new BookingException("Không tìm thấy trạng thái phòng DIRTY trong hệ thống");
            }
        }

        return reservationRepository.save(res);
    }


    @Transactional
    public Reservation addServiceToReservation(Integer reservationId, Integer serviceId, Integer quantity) throws BookingException {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        if (!reservation.isPresent()) {
            throw new BookingException("Không tìm thấy đặt phòng với ID: " + reservationId);
        }

        var service = serviceRepository.findById(serviceId);
        if (!service.isPresent()) {
            throw new BookingException("Không tìm thấy dịch vụ với ID: " + serviceId);
        }

        Reservation res = reservation.get();

        List<Reservation_Service> existingServices = res.getReservation_services();
        Reservation_Service existingService = null;

        if (existingServices != null) {
            for (Reservation_Service rs : existingServices) {
                if (rs.getService().getServiceId() == serviceId) {
                    existingService = rs;
                    break;
                }
            }
        }

        if (existingService != null) {
            existingService.setQuantity(existingService.getQuantity() + quantity);
            existingService.setPriceAtTheTime(existingService.getPriceAtTheTime() + service.get().getPrice() * quantity);
            reservationServiceRepository.save(existingService);
        } else {
            Reservation_Service resService = new Reservation_Service();
            resService.setReservation(res);
            resService.setService(service.get());
            resService.setQuantity(quantity);
            resService.setPriceAtTheTime(service.get().getPrice() * quantity);

            reservationServiceRepository.save(resService);
        }

        double totalAmount = calculateTotalAmount(res);
        res.setTotalAmount(totalAmount);

        return reservationRepository.save(res);
    }

    @Transactional
    public Reservation addMultipleServices(Integer reservationId, java.util.Map<Integer, Integer> servicesToAdd) throws BookingException {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        if (!reservation.isPresent()) {
            throw new BookingException("Không tìm thấy đặt phòng với ID: " + reservationId);
        }

        Reservation res = reservation.get();

        List<Reservation_Service> existingServices = res.getReservation_services();
        if (existingServices == null) {
            existingServices = new java.util.ArrayList<>();
        }

        for (java.util.Map.Entry<Integer, Integer> entry : servicesToAdd.entrySet()) {
            Integer serviceId = entry.getKey();
            Integer quantity = entry.getValue();

            Optional<Service> service = serviceRepository.findById(serviceId);
            if (!service.isPresent()) {
                throw new BookingException("Không tìm thấy dịch vụ với ID: " + serviceId);
            }

            Reservation_Service existingService = null;
            for (Reservation_Service rs : existingServices) {
                if (rs.getService().getServiceId() == serviceId) {
                    existingService = rs;
                    break;
                }
            }

            if (existingService != null) {
                existingService.setQuantity(existingService.getQuantity() + quantity);
                existingService.setPriceAtTheTime(existingService.getPriceAtTheTime() + service.get().getPrice() * quantity);
                reservationServiceRepository.save(existingService);
            } else {
                Reservation_Service resService = new Reservation_Service();
                resService.setReservation(res);
                resService.setService(service.get());
                resService.setQuantity(quantity);
                resService.setPriceAtTheTime(service.get().getPrice() * quantity);

                reservationServiceRepository.save(resService);
                existingServices.add(resService);
            }
        }

        double totalAmount = calculateTotalAmount(res);
        res.setTotalAmount(totalAmount);

        return reservationRepository.save(res);
    }

    public Optional<Reservation> getReservationDetails(Integer reservationId) {
        return reservationRepository.findById(reservationId);
    }

    private double calculateTotalAmount(Reservation reservation) {
        double roomAmount = 0;
        if (reservation.getReservation_rooms() != null && !reservation.getReservation_rooms().isEmpty()) {
            long daysOfStay = getDaysOfStay(reservation.getCheckInDate(), reservation.getCheckOutDate());

            for (Reservation_Room resRoom : reservation.getReservation_rooms()) {
                com.hsf302.hotelmanagement.entity.RoomType roomType = resRoom.getRoom().getRoomType();
                if (roomType != null) {
                    roomAmount += roomType.getBasePrice() * daysOfStay;
                }
            }
        }

        // Tính tiền dịch vụ
        double serviceAmount = 0;
        if (reservation.getReservation_services() != null && !reservation.getReservation_services().isEmpty()) {
            for (Reservation_Service resService : reservation.getReservation_services()) {
                serviceAmount += resService.getPriceAtTheTime();
            }
        }

        return roomAmount + serviceAmount;
    }

    private long getDaysOfStay(Date checkInDate, Date checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            return 1; // Mặc định 1 đêm nếu thiếu dữ liệu
        }

        long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
        long days = diffInMillies / (1000 * 60 * 60 * 24);

        // Đảm bảo tối thiểu 1 đêm
        return Math.max(1, days);
    }
}
