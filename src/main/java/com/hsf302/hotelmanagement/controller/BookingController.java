package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.service.EmailService;
import com.hsf302.hotelmanagement.exception.BookingException;
import com.hsf302.hotelmanagement.service.BookingResult;
import com.hsf302.hotelmanagement.service.BookingService;
import com.hsf302.hotelmanagement.service.GuestService;
import com.hsf302.hotelmanagement.service.GuestService.GuestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private GuestService guestService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/booking")
    public String booking(
            @RequestParam(required = false) String checkInDate,
            @RequestParam(required = false) String checkOutDate,
            @RequestParam(required = false, defaultValue = "1") int adults,
            @RequestParam(required = false, defaultValue = "0") int children,
            Model model) {
        
        List<RoomType> roomTypes = bookingService.getAllRoomTypes();
        Map<Integer, Integer> availableRoomCounts = new HashMap<>();
        for (RoomType roomType : roomTypes) {
            int count = bookingService.getAvailableRoomCount(roomType.getRoomTypeId(), checkInDate, checkOutDate);
            availableRoomCounts.put(roomType.getRoomTypeId(), count);
        }

        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("availableRoomCounts", availableRoomCounts);
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
        
        try {
            RoomType roomType = bookingService.getRoomTypeOrThrow(roomTypeId);

            if (checkInDate == null || checkInDate.trim().isEmpty()) {
                model.addAttribute("error", "Vui lòng chọn ngày nhận phòng.");
                return "redirect:/booking";
            }

            if (checkOutDate == null || checkOutDate.trim().isEmpty()) {
                model.addAttribute("error", "Vui lòng chọn ngày trả phòng.");
                return "redirect:/booking";
            }

            long days = bookingService.calculateDays(checkInDate, checkOutDate);
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
        } catch (BookingException ex) {
            model.addAttribute("error", ex.getMessage());
            return "redirect:/booking";
        }
        
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
            GuestResult guestResult = guestService.findOrCreateGuest(fullName, phoneNumber, email);
            BookingResult bookingResult = bookingService.createBooking(
                    roomTypeId, quantity, checkInDate, checkOutDate, adults, children, notes, guestResult.getGuest()
            );

            if (guestResult.hasRealEmail()) {
                try {
                    emailService.sendBookingConfirmationEmail(
                            guestResult.getEmailUsed(),
                            guestResult.getDisplayName(),
                            bookingResult.getReservation().getReservationId(),
                            bookingResult.getRoomType().getTypeName(),
                            quantity,
                            checkInDate,
                            checkOutDate,
                            bookingResult.getTotalAmount()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("reservationId", bookingResult.getReservation().getReservationId());
            return "redirect:/booking/success";

        } catch (BookingException be) {
            redirectAttributes.addFlashAttribute("error", be.getMessage());
            return "redirect:/booking-details?roomTypeId=" + roomTypeId + "&quantity=" + quantity +
                    "&checkInDate=" + (checkInDate != null ? checkInDate : "") +
                    "&checkOutDate=" + (checkOutDate != null ? checkOutDate : "") +
                    "&adults=" + adults + "&children=" + children;
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
}
