package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.service.EmailService;
import com.hsf302.hotelmanagement.exception.BookingException;
import com.hsf302.hotelmanagement.service.BookingResult;
import com.hsf302.hotelmanagement.service.BookingService;
import com.hsf302.hotelmanagement.service.GuestService;
import com.hsf302.hotelmanagement.service.GuestService.GuestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
            @RequestParam(required = false) Integer roomTypeId,
            @RequestParam(required = false) Integer floorId,
            @RequestParam(required = false, defaultValue = "asc") String priceSort,
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {
        
        List<RoomType> roomTypes = bookingService.getAllRoomTypes();
        List<Floor> floors = bookingService.getAllFloors();

        Integer resolvedRoomTypeId = roomTypeId != null ? roomTypeId : bookingService.pickDefaultRoomTypeId(roomTypes);
        Integer resolvedFloorId = floorId != null ? floorId : bookingService.pickDefaultFloorId(floors);
        String resolvedPriceSort = (priceSort == null || priceSort.trim().isEmpty()) ? "asc" : priceSort;

        Page<Room> roomsPage = bookingService.getAvailableRooms(resolvedRoomTypeId, resolvedFloorId, resolvedPriceSort, page, 5);

        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("floors", floors);
        model.addAttribute("roomsPage", roomsPage);
        model.addAttribute("rooms", roomsPage.getContent());
        model.addAttribute("selectedRoomTypeId", resolvedRoomTypeId);
        model.addAttribute("selectedFloorId", resolvedFloorId);
        model.addAttribute("priceSort", resolvedPriceSort);
        model.addAttribute("currentPage", roomsPage.getNumber());
        model.addAttribute("totalPages", roomsPage.getTotalPages());
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
