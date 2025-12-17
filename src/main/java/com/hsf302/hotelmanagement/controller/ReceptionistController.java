package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.Reservation;
import com.hsf302.hotelmanagement.entity.Service;
import com.hsf302.hotelmanagement.exception.BookingException;
import com.hsf302.hotelmanagement.repository.ReservationRepository;
import com.hsf302.hotelmanagement.repository.ServiceRepository;
import com.hsf302.hotelmanagement.service.ReceptionistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistController {

    @Autowired
    private ReceptionistService receptionistService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Hiển thị danh sách check-in
     */
    @GetMapping("/check-in")
    public String checkInList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false, defaultValue = "") String searchTerm,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate,
            @RequestParam(value = "tab", required = false, defaultValue = "pending") String tab,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        // Mặc định filter ngày hôm nay nếu không có fromDate/toDate
        String today = LocalDate.now().toString();
        if (fromDate == null || fromDate.isEmpty()) {
            fromDate = today;
        }
        if (toDate == null || toDate.isEmpty()) {
            toDate = today;
        }

        // Xác định status dựa trên tab
        // "pending" → "Pending", "checked" → "Confirmed"
        String status = "pending".equals(tab) ? "Pending" : "Confirmed";

        // Gọi service với date range và status
        Page<Reservation> reservations = receptionistService.getReservationsByDateRange(
            searchTerm, fromDate, toDate, status, pageable
        );

        model.addAttribute("reservations", reservations.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", reservations.getTotalPages());
        model.addAttribute("totalElements", reservations.getTotalElements());
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("tab", tab);

        return "receptionist/check-in";
    }

    /**
     * Xử lý check-in khách hàng
     */
    @PostMapping("/check-in")
    public String processCheckIn(
            @RequestParam("reservationId") Integer reservationId,
            @RequestParam("checkInDateTime") String checkInDateTime,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            RedirectAttributes redirectAttributes) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime checkInTime = LocalDateTime.parse(checkInDateTime, formatter);

            Reservation reservation = receptionistService.checkInReservation(reservationId, checkInTime);
            redirectAttributes.addFlashAttribute("successMessage",
                "✓ Check-in thành công cho khách " + reservation.getGuest().getFullName());
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "✗ " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "✗ Lỗi: " + e.getMessage());
        }

        return "redirect:/receptionist/check-in?page=" + page + "&size=" + size;
    }

    /**
     * Hiển thị trang thêm dịch vụ
     */
    @GetMapping("/add-service")
    public String addServiceForm(
            @RequestParam("reservationId") Integer reservationId,
            Model model) {

        Optional<Reservation> reservation = receptionistService.getReservationDetails(reservationId);

        if (reservation.isPresent()) {
            List<Service> services = serviceRepository.findAll();
            model.addAttribute("reservation", reservation.get());
            model.addAttribute("services", services);
            return "receptionist/add-service";
        } else {
            return "redirect:/receptionist/check-in";
        }
    }

    /**
     * Xử lý thêm dịch vụ vào đặt phòng
     */
    @PostMapping("/add-service")
    public String processAddService(
            @RequestParam("reservationId") Integer reservationId,
            @RequestParam("serviceId") Integer serviceId,
            @RequestParam("quantity") Integer quantity,
            RedirectAttributes redirectAttributes) {

        try {
            Reservation reservation = receptionistService.addServiceToReservation(reservationId, serviceId, quantity);
            redirectAttributes.addFlashAttribute("successMessage",
                "✓ Thêm dịch vụ thành công. Tổng tiền: " + String.format("%,.0f", reservation.getTotalAmount()) + "₫");
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "✗ " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "✗ Lỗi: " + e.getMessage());
        }

        return "redirect:/receptionist/check-in";
    }

    /**
     * Xử lý thêm nhiều dịch vụ cùng lúc
     */
    @PostMapping("/add-services-multiple")
    public String processAddServicesMultiple(
            @RequestParam("reservationId") Integer reservationId,
            jakarta.servlet.http.HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            java.util.Map<Integer, Integer> servicesToAdd = new java.util.HashMap<>();

            // Lấy tất cả parameters
            java.util.Enumeration<String> paramNames = request.getParameterNames();

            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();

                // Tìm parameters có dạng "quantity_X"
                if (paramName.startsWith("quantity_")) {
                    String serviceIdStr = paramName.substring(9); // Lấy X từ "quantity_X"

                    try {
                        Integer serviceId = Integer.parseInt(serviceIdStr);
                        Integer quantity = Integer.parseInt(request.getParameter(paramName));

                        if (quantity > 0) {
                            servicesToAdd.put(serviceId, quantity);
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid parameters
                    }
                }
            }

            // Thêm các dịch vụ
            if (!servicesToAdd.isEmpty()) {
                Reservation reservation = receptionistService.addMultipleServices(reservationId, servicesToAdd);
                redirectAttributes.addFlashAttribute("successMessage",
                    "✓ Thêm " + servicesToAdd.size() + " dịch vụ thành công! Tổng tiền: " +
                    String.format("%,.0f", reservation.getTotalAmount()) + "₫");
                return "redirect:/receptionist/check-in";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "✗ Vui lòng chọn ít nhất 1 dịch vụ");
                return "redirect:/receptionist/add-service?reservationId=" + reservationId;
            }
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "✗ " + e.getMessage());
            return "redirect:/receptionist/add-service?reservationId=" + reservationId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "✗ Lỗi: " + e.getMessage());
            return "redirect:/receptionist/add-service?reservationId=" + reservationId;
        }
    }

    /**
     * Xử lý check-out khách hàng
     */
    @PostMapping("/check-out")
    public String processCheckOut(
            @RequestParam("reservationId") Integer reservationId,
            RedirectAttributes redirectAttributes) {

        try {
            Reservation reservation = receptionistService.checkOutReservation(reservationId);
            // Redirect tới invoice để in hóa đơn
            return "redirect:/receptionist/invoice/" + reservationId;
        } catch (BookingException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "✗ " + e.getMessage());
            return "redirect:/receptionist/check-out";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "✗ Lỗi: " + e.getMessage());
            return "redirect:/receptionist/check-out";
        }
    }

    /**
     * Lấy danh sách phòng đang sử dụng (OCCUPIED) - cho mục đích chỉnh sửa dịch vụ
     */
    @GetMapping("/occupied-rooms")
    public String getOccupiedRooms(Model model) {
        try {
            var occupiedRooms = receptionistService.getOccupiedRooms();
            model.addAttribute("occupiedRooms", occupiedRooms);
            model.addAttribute("pageTitle", "Danh Sách Phòng Đang Sử Dụng");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "✗ Lỗi: " + e.getMessage());
        }
        return "receptionist/occupied-rooms";
    }

    /**
     * Hiển thị danh sách phòng cần check-out
     */
    @GetMapping("/check-out")
    public String showCheckOutPage(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        try {
            // Lấy danh sách reservations với status = Confirmed hoặc CheckedOut
            Page<Reservation> reservations;
            Pageable pageable = PageRequest.of(page, size);

            if (search != null && !search.isEmpty()) {
                // Tìm kiếm theo tên khách cho cả Confirmed và CheckedOut
                Page<Reservation> confirmed = reservationRepository.findByStatusAndGuestFullNameContainingIgnoreCase(
                    "Confirmed", search, pageable);
                Page<Reservation> checkedOut = reservationRepository.findByStatusAndGuestFullNameContainingIgnoreCase(
                    "CheckedOut", search, pageable);

                // Merge both lists (simplified - trong thực tế nên dùng custom query)
                java.util.List<Reservation> allReservations = new java.util.ArrayList<>();
                allReservations.addAll(confirmed.getContent());
                allReservations.addAll(checkedOut.getContent());

                // Sort by reservation ID descending
                allReservations.sort((a, b) -> Integer.compare(b.getReservationId(), a.getReservationId()));

                // Create a Page object
                int totalElements = (int) (confirmed.getTotalElements() + checkedOut.getTotalElements());
                reservations = new org.springframework.data.domain.PageImpl<>(
                    allReservations.stream().limit(size).collect(java.util.stream.Collectors.toList()),
                    pageable,
                    totalElements
                );
            } else {
                // Lấy tất cả reservations với status Confirmed hoặc CheckedOut
                Page<Reservation> confirmed = reservationRepository.findByStatus("Confirmed", pageable);
                Page<Reservation> checkedOut = reservationRepository.findByStatus("CheckedOut", pageable);

                // Merge both lists
                java.util.List<Reservation> allReservations = new java.util.ArrayList<>();
                allReservations.addAll(confirmed.getContent());
                allReservations.addAll(checkedOut.getContent());

                // Sort by reservation ID descending
                allReservations.sort((a, b) -> Integer.compare(b.getReservationId(), a.getReservationId()));

                // Create a Page object
                int totalElements = (int) (confirmed.getTotalElements() + checkedOut.getTotalElements());
                reservations = new org.springframework.data.domain.PageImpl<>(
                    allReservations.stream().limit(size).collect(java.util.stream.Collectors.toList()),
                    pageable,
                    totalElements
                );
            }

            model.addAttribute("reservations", reservations);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);

            // Tính totalPages chính xác
            int totalElements = (int) reservations.getTotalElements();
            int totalPages = (totalElements + size - 1) / size;  // Ceiling division
            if (totalPages == 0) totalPages = 1;  // Tối thiểu 1 trang

            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", totalElements);
            model.addAttribute("pageTitle", "Danh Sách Check-out");
            model.addAttribute("activePage", "check-out");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "receptionist/check-out";
    }

    /**
     * Hiển thị hóa đơn check-out
     */
    @GetMapping("/invoice/{reservationId}")
    public String showInvoice(
            @PathVariable("reservationId") Integer reservationId,
            Model model) {
        try {
            var reservation = receptionistService.getReservationDetails(reservationId);
            if (reservation.isPresent()) {
                model.addAttribute("reservation", reservation.get());
                model.addAttribute("pageTitle", "Hóa Đơn Check-out");
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy đặt phòng");
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "receptionist/invoice";
    }

}


