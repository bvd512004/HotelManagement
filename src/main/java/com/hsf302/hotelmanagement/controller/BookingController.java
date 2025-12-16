package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.service.EmailService;
import com.hsf302.hotelmanagement.exception.BookingException;
import com.hsf302.hotelmanagement.service.BookingResult;
import com.hsf302.hotelmanagement.service.BookingService;
import com.hsf302.hotelmanagement.service.EmailService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
            @RequestParam(required = false) String roomTypeId,
            @RequestParam(required = false) String floorId,
            @RequestParam(required = false, defaultValue = "asc") String priceSort,
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {
        
        // Tự động set ngày mặc định: hôm nay và ngày mai
        if (checkInDate == null || checkInDate.trim().isEmpty()) {
            checkInDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (checkOutDate == null || checkOutDate.trim().isEmpty()) {
            checkOutDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        List<RoomType> roomTypes = bookingService.getAllRoomTypes();
        List<Floor> floors = bookingService.getAllFloors();

        // Xử lý roomTypeId và floorId: null hoặc empty string đều được coi là "tất cả"
        Integer resolvedRoomTypeId = null;
        if (roomTypeId != null && !roomTypeId.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(roomTypeId.trim());
                if (id > 0) {
                    resolvedRoomTypeId = id;
                }
            } catch (NumberFormatException e) {
                // Giữ null nếu không parse được
            }
        }

        Integer resolvedFloorId = null;
        if (floorId != null && !floorId.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(floorId.trim());
                if (id > 0) {
                    resolvedFloorId = id;
                }
            } catch (NumberFormatException e) {
                // Giữ null nếu không parse được
            }
        }

        String resolvedPriceSort = (priceSort == null || priceSort.trim().isEmpty()) ? "asc" : priceSort;

        Page<Room> roomsPage = bookingService.getAvailableRooms(
            resolvedRoomTypeId, resolvedFloorId, checkInDate, checkOutDate, resolvedPriceSort, page, 5);

        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("floors", floors);
        model.addAttribute("roomsPage", roomsPage);
        model.addAttribute("rooms", roomsPage.getContent());
        model.addAttribute("selectedRoomTypeId", resolvedRoomTypeId);
        model.addAttribute("selectedFloorId", resolvedFloorId);
        // Thêm option "Tất cả" cho roomType và floor
        model.addAttribute("showAllRoomTypes", resolvedRoomTypeId == null);
        model.addAttribute("showAllFloors", resolvedFloorId == null);
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
            @RequestParam(required = false) Integer roomId,
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
            
            // Lấy thông tin phòng nếu có roomId
            String roomName = null;
            if (roomId != null && roomId > 0) {
                Room room = bookingService.getRoomById(roomId);
                if (room != null) {
                    roomName = room.getRoomName();
                }
            }

            model.addAttribute("roomType", roomType);
            model.addAttribute("roomId", roomId);
            model.addAttribute("roomName", roomName);
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
            @RequestParam int roomNumber,
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
                    roomTypeId,roomNumber, quantity, checkInDate, checkOutDate, adults, children, notes, guestResult.getGuest()
            );

            if (guestResult.hasRealEmail()) {
                try {
                    // Lấy danh sách số phòng từ booking result
                    String roomNumbers = bookingResult.getRoomNumbers();
                    emailService.sendBookingConfirmationEmail(
                            guestResult.getEmailUsed(),
                            guestResult.getDisplayName(),
                            bookingResult.getReservation().getReservationId(),
                            bookingResult.getRoomType().getTypeName(),
                            quantity,
                            roomNumbers,
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
            return "redirect:/booking-details?roomTypeId=" + roomTypeId +
                   "&roomId=" + roomNumber +
                   "&quantity=" + quantity +
                   "&checkInDate=" + (checkInDate != null ? checkInDate : "") +
                   "&checkOutDate=" + (checkOutDate != null ? checkOutDate : "") +
                   "&adults=" + adults + "&children=" + children;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đặt phòng. Vui lòng thử lại.");
            return "redirect:/booking-details?roomTypeId=" + roomTypeId +
                   "&roomId=" + roomNumber +
                   "&quantity=" + quantity +
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
