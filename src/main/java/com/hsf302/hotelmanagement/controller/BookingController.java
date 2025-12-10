package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.*;
import com.hsf302.hotelmanagement.repository.*;
import com.hsf302.hotelmanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class BookingController {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRoomRepository reservationRoomRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/booking")
    public String booking(
            @RequestParam(required = false) String checkInDate,
            @RequestParam(required = false) String checkOutDate,
            @RequestParam(required = false, defaultValue = "1") int adults,
            @RequestParam(required = false, defaultValue = "0") int children,
            Model model) {
        
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("adults", adults);
        model.addAttribute("children", children);
        
        return "booking";
    }

    @GetMapping("/booking-details")
    public String bookingDetails(
            @RequestParam int roomTypeId,
            @RequestParam int quantity,
            @RequestParam(required = false) String checkInDate,
            @RequestParam(required = false) String checkOutDate,
            @RequestParam int adults,
            @RequestParam int children,
            Model model) {
        
        RoomType roomType = roomTypeRepository.findById(roomTypeId).orElse(null);
        if (roomType == null) {
            return "redirect:/booking";
        }
        

        if (checkInDate == null || checkInDate.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng chọn ngày nhận phòng.");
            return "redirect:/booking";
        }
        
        if (checkOutDate == null || checkOutDate.trim().isEmpty()) {
            model.addAttribute("error", "Vui lòng chọn ngày trả phòng.");
            return "redirect:/booking";
        }
        

        long days = calculateDays(checkInDate, checkOutDate);
        double totalPrice = roomType.getBasePrice() * quantity * days;
        
        model.addAttribute("roomType", roomType);
        model.addAttribute("quantity", quantity);
        model.addAttribute("checkInDate", checkInDate);
        model.addAttribute("checkOutDate", checkOutDate);
        model.addAttribute("adults", adults);
        model.addAttribute("children", children);
        model.addAttribute("days", days);
        model.addAttribute("totalPrice", totalPrice);
        
        return "booking-details";
    }

    @PostMapping("/booking/complete")
    public String completeBooking(
            @RequestParam int roomTypeId,
            @RequestParam int quantity,
            @RequestParam(required = false) String checkInDate,
            @RequestParam(required = false) String checkOutDate,
            @RequestParam int adults,
            @RequestParam int children,
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (checkInDate == null || checkInDate.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn ngày nhận phòng.");
                return "redirect:/booking-details?roomTypeId=" + roomTypeId + "&quantity=" + quantity + 
                       "&checkInDate=" + (checkInDate != null ? checkInDate : "") + 
                       "&checkOutDate=" + (checkOutDate != null ? checkOutDate : "") + 
                       "&adults=" + adults + "&children=" + children;
            }
            
            if (checkOutDate == null || checkOutDate.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn ngày trả phòng.");
                return "redirect:/booking-details?roomTypeId=" + roomTypeId + "&quantity=" + quantity + 
                       "&checkInDate=" + checkInDate + 
                       "&checkOutDate=" + (checkOutDate != null ? checkOutDate : "") + 
                       "&adults=" + adults + "&children=" + children;
            }


            String[] nameParts = fullName.trim().split("\\s+", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : fullName;
            String lastName = nameParts.length > 1 ? nameParts[1] : "";


            Guest guest;
            String guestEmail = email != null && !email.isEmpty() ? email : phoneNumber + "@temp.com";
            Optional<Guest> existingGuest = guestRepository.findByEmail(guestEmail);
            
            if (existingGuest.isPresent()) {
                guest = existingGuest.get();
                if (email != null && !email.isEmpty()) {
                    guest.setEmail(email);
                }
                guest.setPhoneNumber(phoneNumber);
                guest.setFirstName(firstName);
                guest.setLastName(lastName);
            } else {
                guest = new Guest();
                guest.setFirstName(firstName);
                guest.setLastName(lastName);
                guest.setEmail(guestEmail);
                guest.setPhoneNumber(phoneNumber);
            }
            guest = guestRepository.save(guest);


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date checkIn = sdf.parse(checkInDate.trim());
            Date checkOut = sdf.parse(checkOutDate.trim());
            
            // Validate date range
            if (checkOut.before(checkIn) || checkOut.equals(checkIn)) {
                redirectAttributes.addFlashAttribute("error", "Ngày trả phòng phải sau ngày nhận phòng.");
                return "redirect:/booking-details?roomTypeId=" + roomTypeId + "&quantity=" + quantity + 
                       "&checkInDate=" + checkInDate + "&checkOutDate=" + checkOutDate + 
                       "&adults=" + adults + "&children=" + children;
            }

            // Tính số ngày và tổng tiền
            long days = calculateDays(checkInDate, checkOutDate);
            RoomType roomType = roomTypeRepository.findById(roomTypeId).orElseThrow();
            double totalAmount = roomType.getBasePrice() * quantity * days;

            // Tìm phòng available
            List<Room> availableRooms = roomRepository.findAvailableRoomsByType(roomTypeId);
            if (availableRooms.size() < quantity) {
                redirectAttributes.addFlashAttribute("error", "Không đủ phòng trống. Chỉ còn " + availableRooms.size() + " phòng.");
                return "redirect:/booking-details?roomTypeId=" + roomTypeId + "&quantity=" + quantity + 
                       "&checkInDate=" + checkInDate + "&checkOutDate=" + checkOutDate + 
                       "&adults=" + adults + "&children=" + children;
            }


            Reservation reservation = new Reservation();
            reservation.setCheckInDate(checkIn);
            reservation.setCheckOutDate(checkOut);
            reservation.setNumberOfGuests(adults + children);
            reservation.setTotalAmount(totalAmount);
            reservation.setStatus("Pending");
            reservation.setGuest(guest);
            reservation = reservationRepository.save(reservation);

            // Tạo Reservation_Room cho mỗi phòng
            List<Reservation_Room> reservationRooms = new ArrayList<>();
            for (int i = 0; i < quantity && i < availableRooms.size(); i++) {
                Room room = availableRooms.get(i);
                Reservation_Room reservationRoom = new Reservation_Room();
                reservationRoom.setReservationId(reservation);
                reservationRoom.setRoom(room);
                reservationRoom.setStatus("Reserved");
                reservationRoom.setNote(notes);
                reservationRooms.add(reservationRoom);
            }

            // Lưu Reservation_Room
            for (Reservation_Room rr : reservationRooms) {
                reservationRoomRepository.save(rr);
            }


            if (email != null && !email.isEmpty() && !email.equals(phoneNumber + "@temp.com")) {
                try {
                    String guestName = firstName + (lastName != null && !lastName.isEmpty() ? " " + lastName : "");
                    emailService.sendBookingConfirmationEmail(
                        email,
                        guestName,
                        reservation.getReservationId(),
                        roomType.getTypeName(),
                        quantity,
                        checkInDate,
                        checkOutDate,
                        totalAmount
                    );
                } catch (Exception e) {
                    // Log error nhưng không ảnh hưởng đến luồng đặt phòng
                    e.printStackTrace();
                }
            }

            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("reservationId", reservation.getReservationId());
            return "redirect:/booking/success";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đặt phòng. Vui lòng thử lại.");
            return "redirect:/booking-details?roomTypeId=" + roomTypeId + "&quantity=" + quantity + 
                   "&checkInDate=" + checkInDate + "&checkOutDate=" + checkOutDate + 
                   "&adults=" + adults + "&children=" + children;
        }
    }

    @GetMapping("/booking/success")
    public String bookingSuccess(Model model) {
        // Lấy thông tin từ flash attributes
        if (model.containsAttribute("reservationId")) {
            Integer reservationId = (Integer) model.getAttribute("reservationId");
            model.addAttribute("reservationId", reservationId);
        }
        return "booking-success";
    }

    private long calculateDays(String checkInDate, String checkOutDate) {
        try {
            if (checkInDate == null || checkInDate.trim().isEmpty() || 
                checkOutDate == null || checkOutDate.trim().isEmpty()) {
                return 1; // Mặc định 1 ngày nếu thiếu thông tin
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date checkIn = sdf.parse(checkInDate.trim());
            Date checkOut = sdf.parse(checkOutDate.trim());
            long diff = checkOut.getTime() - checkIn.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            return days > 0 ? days : 1; // Tối thiểu 1 ngày
        } catch (ParseException e) {
            return 1; // Mặc định 1 ngày nếu lỗi
        }
    }
}

