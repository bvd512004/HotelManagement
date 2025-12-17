package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.dto.response.ReservationDTO;
import com.hsf302.hotelmanagement.dto.response.overviewReservationDTO; // Import the new DTO
import com.hsf302.hotelmanagement.entity.*;
import com.hsf302.hotelmanagement.repository.GuestRepository;
import com.hsf302.hotelmanagement.service.ReservationService;
import com.hsf302.hotelmanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private GuestRepository guestRepository;

    @GetMapping
    public String listReservations(Model model) {
        model.addAttribute("reservations", reservationService.findAll());
        return "reservation-list";
    }

    @GetMapping("/{id}")
    public String viewReservation(@PathVariable("id") int id, Model model) {
        Reservation reservation = reservationService.findById(id);
        model.addAttribute("reservation", reservation);
        model.addAttribute("view", "reservation-details");
        return "reservation-details";
    }

    @GetMapping("/room/{roomId}")
    public String listReservationsForRoom(@PathVariable("roomId") int roomId, Model model) {
        List<Reservation> reservations = reservationService.findByRoomId(roomId);
        Room room = roomService.findById(roomId);
        model.addAttribute("reservations", reservations);
        model.addAttribute("roomName", room != null ? room.getRoomName() : "");
        model.addAttribute("view", "room-reservation-list");
        return "room-reservation-list";
    }

    // API để kiểm tra phòng có khả dụng không
    @GetMapping("/api/check-availability")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @RequestParam int roomId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);

            Room room = roomService.findById(roomId);
            if (room == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("available", false);
                response.put("message", "Phòng không tồn tại");
                return ResponseEntity.ok(response);
            }

            // Kiểm tra trạng thái phòng
            if (room.getRoomStatus() != null) {
                String status = room.getRoomStatus().getRoomStatus();
                if (status.equals("Occupied") || status.equals("Dirty")) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("available", false);
                    response.put("message", "Phòng hiện tại đang " + status);
                    return ResponseEntity.ok(response);
                }
            }

            // Kiểm tra có reservation trùng khoảng thời gian không
            List<Reservation> conflictingReservations = reservationService.findConflictingReservations(roomId, checkIn, checkOut);

            Map<String, Object> response = new HashMap<>();
            if (conflictingReservations.isEmpty()) {
                response.put("available", true);
                response.put("message", "Phòng khả dụng");
            } else {
                response.put("available", false);
                response.put("message", "Phòng đã được đặt trong khoảng thời gian này");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("available", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // API để lấy thông tin phòng
    @GetMapping("/api/room/{roomId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRoomDetails(@PathVariable int roomId) {
        Room room = roomService.findById(roomId);
        if (room == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("roomId", room.getRoomId());
        response.put("roomName", room.getRoomName());
        response.put("typeName", room.getRoomType().getTypeName());
        response.put("roomTypeId", room.getRoomType().getRoomTypeId());
        response.put("basePrice", room.getRoomType().getBasePrice());
        response.put("capacity", room.getRoomType().getCapacity());
        response.put("description", room.getRoomType().getDescription());

        return ResponseEntity.ok(response);
    }

    // API để lấy danh sách dịch vụ
    @GetMapping("/api/services")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getServices() {
        List<Service> services = reservationService.getAllServices();
        List<Map<String, Object>> serviceList = new ArrayList<>();

        for (Service service : services) {
            Map<String, Object> serviceMap = new HashMap<>();
            serviceMap.put("serviceId", service.getServiceId());
            serviceMap.put("serviceName", service.getServiceName());
            serviceMap.put("price", service.getPrice());
            serviceMap.put("unit", service.getUnit());
            serviceMap.put("category", service.getCategory());
            serviceList.add(serviceMap);
        }

        return ResponseEntity.ok(serviceList);
    }

    // API để tính tiền
    @PostMapping("/api/calculate-price")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> calculatePrice(@RequestBody Map<String, Object> data) {
        try {
            int roomTypeId = ((Number) data.get("roomTypeId")).intValue();
            String checkInDate = (String) data.get("checkInDate");
            String checkOutDate = (String) data.get("checkOutDate");

            RoomType roomType = roomService.getRoomTypeById(roomTypeId);
            if (roomType == null) {
                return ResponseEntity.badRequest().body(null);
            }

            // Tính số đêm
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date checkIn = sdf.parse(checkInDate);
            Date checkOut = sdf.parse(checkOutDate);
            long diffTime = checkOut.getTime() - checkIn.getTime();
            long nights = diffTime / (1000 * 60 * 60 * 24);
            if (nights <= 0) nights = 1;

            double totalRoomPrice = roomType.getBasePrice() * nights;

            // Tính tiền dịch vụ nếu có
            double totalServicePrice = 0;
            if (data.containsKey("services")) {
                List<Map<String, Object>> services = (List<Map<String, Object>>) data.get("services");
                for (Map<String, Object> service : services) {
                    int serviceId = ((Number) service.get("serviceId")).intValue();
                    int quantity = ((Number) service.get("quantity")).intValue();

                    Service svc = reservationService.getAllServices().stream()
                            .filter(s -> s.getServiceId() == serviceId)
                            .findFirst()
                            .orElse(null);

                    if (svc != null) {
                        totalServicePrice += svc.getPrice() * quantity;
                    }
                }
            }

            double grandTotal = totalRoomPrice + totalServicePrice;

            Map<String, Object> response = new HashMap<>();
            response.put("nights", nights);
            response.put("totalRoomPrice", totalRoomPrice);
            response.put("totalServicePrice", totalServicePrice);
            response.put("grandTotal", grandTotal);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    // API để lưu reservation
    @PostMapping("/api/save")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> saveReservation(@RequestBody ReservationDTO dto) {
        try {
            // Tìm hoặc tạo guest mới
            Guest guest = null;
            if (dto.getGuestId() > 0) {
                guest = guestRepository.findById(dto.getGuestId()).orElse(null);
            }

            if (guest == null) {
                guest = new Guest();
                guest.setFirstName(dto.getFirstName());
                guest.setLastName(dto.getLastName());
                guest.setEmail(dto.getEmail());
                guest.setPhoneNumber(dto.getPhoneNumber());
                guest.setAddress(dto.getAddress());
                guest = guestRepository.save(guest);
            }

            // Tạo reservation
            Reservation reservation = new Reservation();

            // Parse dates
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date checkInDate = sdf.parse(dto.getCheckInDate());
            Date checkOutDate = sdf.parse(dto.getCheckOutDate());
            reservation.setCheckInDate(checkInDate);
            reservation.setCheckOutDate(checkOutDate);
            reservation.setNumberOfGuests(dto.getNumberOfGuests());
            reservation.setTotalAmount(dto.getGrandTotal());
            reservation.setStatus("Pending");
            reservation.setCreatedAt(LocalDateTime.now());
            reservation.setGuest(guest);

            // Tạo reservation_rooms
            List<Reservation_Room> reservationRooms = new ArrayList<>();
            Room room = roomService.findById(dto.getRoomId());
            Reservation_Room resRoom = new Reservation_Room();
            resRoom.setRoom(room);
            resRoom.setStatus("Reserved");
            resRoom.setNote("");
            reservationRooms.add(resRoom);
            reservation.setReservation_rooms(reservationRooms);

            // Set reservation cho các room
            for (Reservation_Room rr : reservationRooms) {
                rr.setReservation(reservation);
            }

            // Tính toán lại tổng tiền ở backend
            double totalRoomPrice = 0;
            if (room != null && room.getRoomType() != null) {
                long diffInMillies = Math.abs(checkOutDate.getTime() - checkInDate.getTime());
                long nights = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if (nights == 0) nights = 1; // Minimum 1 night
                totalRoomPrice = room.getRoomType().getBasePrice() * nights;
            }

            double totalServicePrice = 0;
            List<Reservation_Service> reservationServices = new ArrayList<>();
            if (dto.getServiceIds() != null && !dto.getServiceIds().isEmpty()) {
                for (int i = 0; i < dto.getServiceIds().size(); i++) {
                    int serviceId = dto.getServiceIds().get(i);
                    int quantity = dto.getServiceQuantities() != null && i < dto.getServiceQuantities().size()
                            ? dto.getServiceQuantities().get(i) : 1;

                    Service service = reservationService.getServiceById(serviceId);

                    if (service != null) {
                        totalServicePrice += service.getPrice() * quantity;
                        Reservation_Service resService = new Reservation_Service();
                        resService.setService(service);
                        resService.setQuantity(quantity);
                        resService.setPriceAtTheTime(service.getPrice());
                        resService.setReservation(reservation);
                        reservationServices.add(resService);
                    }
                }
            }
            reservation.setReservation_services(reservationServices);

            double grandTotal = totalRoomPrice + totalServicePrice;
            reservation.setTotalAmount(grandTotal);


            // Lưu reservation
            Reservation savedReservation = reservationService.save(reservation);

            // Cập nhật trạng thái phòng thành Reserved
            if (room != null) {
                Room_Status reservedStatus = null;
                List<Room_Status> allStatuses = roomService.getAllRoomStatuses();
                for (Room_Status status : allStatuses) {
                    if (status.getRoomStatus().equals("Available")) {
                        reservedStatus = status;
                        break;
                    }
                }

                if (reservedStatus != null) {
                    room.setRoomStatus(reservedStatus);
                    roomService.save(room);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reservationId", savedReservation.getReservationId());
            response.put("message", "Đặt phòng thành công");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/manager/reservations-fragment")
    public String showReservationList(@RequestParam(value = "filter", required = false, defaultValue = "all") String filter, Model model) {
        List<Reservation> reservations;
        if ("today".equals(filter)) {
            reservations = reservationService.getReservationForToday();
        } else if ("month".equals(filter)) {
            reservations = reservationService.getReservationForThisMonth();
        } else {
            reservations = reservationService.getAllReservations();
        }

        List<overviewReservationDTO> reservationList = new ArrayList<>();

        for (Reservation res : reservations) {
            if (res.getReservation_rooms() != null) {
                for (Reservation_Room reservationRoom : res.getReservation_rooms()) {
                    // Add more comprehensive null checks
                    if (res.getGuest() != null && reservationRoom.getRoom() != null &&
                        reservationRoom.getRoom().getRoomType() != null &&
                        res.getCheckInDate() != null && res.getCheckOutDate() != null) {
                        try {
                            String guestName = res.getGuest().getFirstName() + " " + res.getGuest().getLastName();
                            String roomName = reservationRoom.getRoom().getRoomName();

                            overviewReservationDTO dto = new overviewReservationDTO(
                                    res.getReservationId(),
                                    guestName,
                                    roomName,
                                    res.getCheckInDate(),
                                    res.getCheckOutDate(),
                                    res.getNumberOfGuests(),
                                    res.getTotalAmount() // Use the total amount from the reservation object
                            );
                            reservationList.add(dto);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        model.addAttribute("resList", reservationList);
        model.addAttribute("filter", filter);
        return "manager/reservationList :: reservation-content";
    }

}
