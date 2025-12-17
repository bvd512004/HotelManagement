package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Reservation;
import com.hsf302.hotelmanagement.entity.Reservation_Room;
import com.hsf302.hotelmanagement.entity.Reservation_Service;
import com.hsf302.hotelmanagement.exception.BookingException;
import com.hsf302.hotelmanagement.repository.ReservationRepository;
import com.hsf302.hotelmanagement.repository.ReservationRoomRepository;
import com.hsf302.hotelmanagement.repository.ReservationServiceRepository;
import com.hsf302.hotelmanagement.repository.ServiceRepository;
import com.hsf302.hotelmanagement.repository.RoomRepository;
import com.hsf302.hotelmanagement.repository.Room_StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
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
    private Room_StatusRepository roomStatusRepository;

    /**
     * Lấy danh sách reservations theo date range và status
     * @param searchTerm tìm kiếm theo tên/email
     * @param fromDate ngày bắt đầu (yyyy-MM-dd)
     * @param toDate ngày kết thúc (yyyy-MM-dd)
     * @param status "Pending" hoặc "CheckedIn"
     * @param pageable phân trang
     * @return Page of Reservations
     */
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

    /**
     * Lấy ngày bắt đầu của hôm nay
     */
    private Date getStartOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * Lấy ngày kết thúc của hôm nay
     */
    private Date getEndOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * Lấy danh sách check-in theo khoảng ngày với tìm kiếm tùy chọn
     * @param searchTerm tìm kiếm theo tên, email khách hàng
     * @param dateRange khoảng ngày: "today" (1 ngày), "week" (1 tuần), "month" (1 tháng)
     * @param pageable phân trang
     * @return trang chứa danh sách đặt phòng
     */
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

    /**
     * Tính toán khoảng ngày dựa trên lựa chọn
     * @param dateRange "today", "week", "month"
     * @return mảng [startDate, endDate]
     */
    private Date[] getDateRange(String dateRange) {
        Calendar cal = Calendar.getInstance();

        // Thiết lập startDate là đầu ngày hôm nay
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();

        // Mặc định là 1 ngày
        if (dateRange == null || dateRange.equals("today")) {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        } else if (dateRange.equals("week")) {
            // Cộng thêm 7 ngày
            cal.add(Calendar.DAY_OF_MONTH, 7);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        } else if (dateRange.equals("month")) {
            // Cộng thêm 30 ngày
            cal.add(Calendar.DAY_OF_MONTH, 30);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        }

        Date endDate = cal.getTime();
        return new Date[]{startDate, endDate};
    }

    /**
     * Xác nhận check-in khách hàng
     * @param reservationId ID của đặt phòng
     * @param checkInDateTime thời gian check-in
     * @throws BookingException nếu không tìm thấy đặt phòng
     */
    @Transactional
    public Reservation checkInReservation(Integer reservationId, LocalDateTime checkInDateTime) throws BookingException {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);

        if (!reservation.isPresent()) {
            throw new BookingException("Không tìm thấy đặt phòng với ID: " + reservationId);
        }

        Reservation res = reservation.get();
        res.setStatus("Confirmed");

        // Cập nhật trạng thái của Reservation_Room thành "CheckedIn"
        List<Reservation_Room> rooms = res.getReservation_rooms();
        if (rooms != null && !rooms.isEmpty()) {
            // Lấy Room_Status OCCUPIED từ database
            Optional<com.hsf302.hotelmanagement.entity.Room_Status> occupiedStatus =
                roomStatusRepository.findByRoomStatus("OCCUPIED");

            if (occupiedStatus.isPresent()) {
                for (Reservation_Room resRoom : rooms) {
                    // Cập nhật Reservation_Room status
                    resRoom.setStatus("CheckedIn");
                    reservationRoomRepository.save(resRoom);

                    // Cập nhật Room status thành OCCUPIED
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

    /**
     * Xác nhận check-out khách hàng
     * @param reservationId ID của đặt phòng
     * @throws BookingException nếu không tìm thấy đặt phòng
     */
    @Transactional
    public Reservation checkOutReservation(Integer reservationId) throws BookingException {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);

        if (!reservation.isPresent()) {
            throw new BookingException("Không tìm thấy đặt phòng với ID: " + reservationId);
        }

        Reservation res = reservation.get();
        res.setStatus("CheckedOut");

        // Cập nhật trạng thái của Reservation_Room thành "CheckedOut"
        List<Reservation_Room> rooms = res.getReservation_rooms();
        if (rooms != null && !rooms.isEmpty()) {
            // Lấy Room_Status DIRTY từ database
            Optional<com.hsf302.hotelmanagement.entity.Room_Status> dirtyStatus =
                roomStatusRepository.findByRoomStatus("DIRTY");

            if (dirtyStatus.isPresent()) {
                for (Reservation_Room resRoom : rooms) {
                    // Cập nhật Reservation_Room status
                    resRoom.setStatus("CheckedOut");
                    reservationRoomRepository.save(resRoom);

                    // Cập nhật Room status thành DIRTY
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

    /**
     * Thêm dịch vụ vào đặt phòng và cập nhật tổng tiền
     * Tổng tiền = (Giá phòng × Số đêm) + Tổng dịch vụ
     * @param reservationId ID của đặt phòng
     * @param serviceId ID của dịch vụ
     * @param quantity số lượng
     * @throws BookingException nếu không tìm thấy dịch vụ hoặc đặt phòng
     */
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

        // Kiểm tra xem dịch vụ đã tồn tại trong reservation chưa
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
            // Dịch vụ đã tồn tại → cập nhật quantity
            existingService.setQuantity(existingService.getQuantity() + quantity);
            existingService.setPriceAtTheTime(existingService.getPriceAtTheTime() + service.get().getPrice() * quantity);
            reservationServiceRepository.save(existingService);
        } else {
            // Dịch vụ chưa tồn tại → tạo mới
            Reservation_Service resService = new Reservation_Service();
            resService.setReservation(res);
            resService.setService(service.get());
            resService.setQuantity(quantity);
            resService.setPriceAtTheTime(service.get().getPrice() * quantity);

            reservationServiceRepository.save(resService);
        }

        // CẬP NHẬT: Tính lại tổng tiền từ đầu (Phòng + Dịch vụ)
        double totalAmount = calculateTotalAmount(res);
        res.setTotalAmount(totalAmount);

        return reservationRepository.save(res);
    }

    /**
     * Thêm nhiều dịch vụ vào đặt phòng cùng một lúc
     * Tổng tiền = (Giá phòng × Số đêm) + Tổng dịch vụ
     * @param reservationId ID của đặt phòng
     * @param servicesToAdd Map của serviceId -> quantity
     * @throws BookingException nếu có lỗi
     */
    @Transactional
    public Reservation addMultipleServices(Integer reservationId, java.util.Map<Integer, Integer> servicesToAdd) throws BookingException {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        if (!reservation.isPresent()) {
            throw new BookingException("Không tìm thấy đặt phòng với ID: " + reservationId);
        }

        Reservation res = reservation.get();

        // Lấy danh sách dịch vụ hiện tại của reservation
        List<Reservation_Service> existingServices = res.getReservation_services();
        if (existingServices == null) {
            existingServices = new java.util.ArrayList<>();
        }

        // Thêm từng dịch vụ
        for (java.util.Map.Entry<Integer, Integer> entry : servicesToAdd.entrySet()) {
            Integer serviceId = entry.getKey();
            Integer quantity = entry.getValue();

            Optional<com.hsf302.hotelmanagement.entity.Service> service = serviceRepository.findById(serviceId);
            if (!service.isPresent()) {
                throw new BookingException("Không tìm thấy dịch vụ với ID: " + serviceId);
            }

            // Kiểm tra xem dịch vụ đã tồn tại chưa
            Reservation_Service existingService = null;
            for (Reservation_Service rs : existingServices) {
                if (rs.getService().getServiceId() == serviceId) {
                    existingService = rs;
                    break;
                }
            }

            if (existingService != null) {
                // Dịch vụ đã tồn tại → cập nhật quantity
                existingService.setQuantity(existingService.getQuantity() + quantity);
                existingService.setPriceAtTheTime(existingService.getPriceAtTheTime() + service.get().getPrice() * quantity);
                reservationServiceRepository.save(existingService);
            } else {
                // Dịch vụ chưa tồn tại → tạo mới
                Reservation_Service resService = new Reservation_Service();
                resService.setReservation(res);
                resService.setService(service.get());
                resService.setQuantity(quantity);
                resService.setPriceAtTheTime(service.get().getPrice() * quantity);

                reservationServiceRepository.save(resService);
                existingServices.add(resService);
            }
        }

        // CẬP NHẬT: Tính lại tổng tiền từ đầu (Phòng + Dịch vụ)
        double totalAmount = calculateTotalAmount(res);
        res.setTotalAmount(totalAmount);

        return reservationRepository.save(res);
    }

    /**
     * Lấy chi tiết đặt phòng
     */
    public Optional<Reservation> getReservationDetails(Integer reservationId) {
        return reservationRepository.findById(reservationId);
    }

    /**
     * Tính tổng tiền = (Giá phòng × Số đêm) + Tổng dịch vụ
     * @param reservation đối tượng Reservation
     * @return tổng tiền cần thanh toán
     */
    private double calculateTotalAmount(Reservation reservation) {
        // Tính tiền phòng = BasePrice × Số đêm
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

    /**
     * Tính số đêm lưu trú
     * @param checkInDate ngày nhận phòng
     * @param checkOutDate ngày trả phòng
     * @return số đêm lưu trú (tối thiểu 1 đêm)
     */
    private long getDaysOfStay(Date checkInDate, Date checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            return 1; // Mặc định 1 đêm nếu thiếu dữ liệu
        }

        long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
        long days = diffInMillies / (1000 * 60 * 60 * 24);

        // Đảm bảo tối thiểu 1 đêm
        return Math.max(1, days);
    }

    /**
     * Lấy danh sách phòng đang sử dụng (OCCUPIED)
     */
    public List<com.hsf302.hotelmanagement.entity.Room> getOccupiedRooms() {
        // Lấy Room_Status với status = "OCCUPIED"
        Optional<com.hsf302.hotelmanagement.entity.Room_Status> occupiedStatus =
            roomStatusRepository.findByRoomStatus("OCCUPIED");

        if (!occupiedStatus.isPresent()) {
            return new java.util.ArrayList<>();
        }

        // Lấy tất cả phòng với status = OCCUPIED
        return roomRepository.findByRoomStatus(occupiedStatus.get());
    }
}

