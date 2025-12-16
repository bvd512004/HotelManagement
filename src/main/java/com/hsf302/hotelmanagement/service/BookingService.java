package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Guest;
import com.hsf302.hotelmanagement.entity.Reservation;
import com.hsf302.hotelmanagement.entity.Reservation_Room;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.exception.BookingException;
import com.hsf302.hotelmanagement.repository.FloorRepository;
import com.hsf302.hotelmanagement.repository.ReservationRepository;
import com.hsf302.hotelmanagement.repository.ReservationRoomRepository;
import com.hsf302.hotelmanagement.repository.RoomRepository;
import com.hsf302.hotelmanagement.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationRoomRepository reservationRoomRepository;

    @Autowired
    private FloorRepository floorRepository;

    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    public RoomType getRoomTypeOrThrow(int roomTypeId) {
        return roomTypeRepository.findById(roomTypeId).orElseThrow(() ->
                new BookingException("Không tìm thấy hạng phòng."));
    }

    public Room getRoomById(int roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public int getAvailableRoomCount(int roomTypeId, String checkInDate, String checkOutDate) {
        try {
            if (checkInDate == null || checkInDate.trim().isEmpty() ||
                checkOutDate == null || checkOutDate.trim().isEmpty()) {
                return roomRepository.countByRoomTypeId(roomTypeId);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date checkIn = sdf.parse(checkInDate.trim());
            Date checkOut = sdf.parse(checkOutDate.trim());
            return roomRepository.findAvailableRoomsByTypeAndDate(roomTypeId, checkIn, checkOut).size();
        } catch (ParseException e) {
            return roomRepository.countByRoomTypeId(roomTypeId);
        }
    }

    public List<Floor> getAllFloors() {
        return floorRepository.findAll(Sort.by("floorNumber"));
    }



    public Integer pickDefaultRoomTypeId(List<RoomType> roomTypes) {
        if (roomTypes == null || roomTypes.isEmpty()) {
            return null;
        }
        return roomTypes.stream()
                .filter(rt -> rt.getTypeName() != null && rt.getTypeName().equalsIgnoreCase("standard"))
                .map(RoomType::getRoomTypeId)
                .findFirst()
                .orElse(roomTypes.get(0).getRoomTypeId());
    }

    public Integer pickDefaultFloorId(List<Floor> floors) {
        if (floors == null || floors.isEmpty()) {
            return null;
        }
        return floors.stream()
                .filter(f -> f.getFloorNumber() == 1)
                .map(Floor::getFloorId)
                .findFirst()
                .orElse(floors.get(0).getFloorId());
    }

    public Page<Room> getAvailableRooms(Integer roomTypeId,
                                        Integer floorId,
                                        String checkInDate,
                                        String checkOutDate,
                                        String priceSort,
                                        int page,
                                        int size) {
        Sort sort = Sort.by("roomType.basePrice");
        if ("desc".equalsIgnoreCase(priceSort)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), size, sort);
        
        // Nếu có ngày check-in và check-out, lọc theo ngày để loại bỏ phòng đã được đặt
        if (checkInDate != null && !checkInDate.trim().isEmpty() &&
            checkOutDate != null && !checkOutDate.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date checkIn = sdf.parse(checkInDate.trim());
                Date checkOut = sdf.parse(checkOutDate.trim());
                return roomRepository.findAvailableRoomsFilteredByDate(roomTypeId, floorId, checkIn, checkOut, pageable);
            } catch (ParseException e) {
                // Nếu parse lỗi, fallback về query không lọc theo ngày
                return roomRepository.findAvailableRoomsFiltered(roomTypeId, floorId, pageable);
            }
        }
        
        // Nếu không có ngày, hiển thị tất cả phòng available
        return roomRepository.findAvailableRoomsFiltered(roomTypeId, floorId, pageable);
    }

    public long calculateDays(String checkInDate, String checkOutDate) {
        try {
            if (checkInDate == null || checkInDate.trim().isEmpty() ||
                checkOutDate == null || checkOutDate.trim().isEmpty()) {
                return 1;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date checkIn = sdf.parse(checkInDate.trim());
            Date checkOut = sdf.parse(checkOutDate.trim());
            long diff = checkOut.getTime() - checkIn.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            return days > 0 ? days : 1;
        } catch (ParseException e) {
            return 1;
        }
    }

    public BookingResult createBooking(int roomTypeId,
                                       int roomId,
                                       int quantity,
                                       String checkInDate,
                                       String checkOutDate,
                                       int adults,
                                       int children,
                                       String notes,
                                       Guest guest) {
        try {
            validateDateString(checkInDate, "Vui lòng chọn ngày nhận phòng.");
            validateDateString(checkOutDate, "Vui lòng chọn ngày trả phòng.");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date checkIn = sdf.parse(checkInDate.trim());
            Date checkOut = sdf.parse(checkOutDate.trim());

            if (checkOut.before(checkIn) || checkOut.equals(checkIn)) {
                throw new BookingException("Ngày trả phòng phải sau ngày nhận phòng.");
            }

            RoomType roomType = getRoomTypeOrThrow(roomTypeId);
            long days = calculateDays(checkInDate, checkOutDate);
            double totalAmount = roomType.getBasePrice() * quantity * days;

            List<Room> availableRooms = roomRepository.findAvailableRoomsByTypeAndDate(roomTypeId, checkIn, checkOut);

            // Nếu có roomId cụ thể, kiểm tra phòng đó có available không
            if (roomId > 0) {
                Room selectedRoom = getRoomById(roomId);
                if (selectedRoom == null) {
                    throw new BookingException("Không tìm thấy phòng được chọn.");
                }
                // Kiểm tra phòng có cùng roomType không
                if (selectedRoom.getRoomType().getRoomTypeId() != roomTypeId) {
                    throw new BookingException("Phòng được chọn không khớp với hạng phòng.");
                }
                // Kiểm tra phòng có available không
                boolean isAvailable = availableRooms.stream()
                        .anyMatch(r -> r.getRoomId() == roomId);
                if (!isAvailable) {
                    throw new BookingException("Phòng đã được đặt trong khoảng thời gian này.");
                }
            }

            if (availableRooms.size() < quantity) {
                throw new BookingException("Không đủ phòng trống. Chỉ còn " + availableRooms.size() + " phòng.");
            }

            Reservation reservation = new Reservation();
            reservation.setCheckInDate(checkIn);
            reservation.setCheckOutDate(checkOut);
            reservation.setNumberOfGuests(adults + children);
            reservation.setTotalAmount(totalAmount);
            reservation.setStatus("Pending");
            reservation.setGuest(guest);
            reservation = reservationRepository.save(reservation);

            List<Reservation_Room> reservationRooms = new ArrayList<>();

            // Nếu có roomId cụ thể, đặt phòng đó trước
            if (roomId > 0) {
                Room selectedRoom = getRoomById(roomId);
                if (selectedRoom != null) {
                    Reservation_Room reservationRoom = new Reservation_Room();
                    reservationRoom.setReservation(reservation);
                    reservationRoom.setRoom(selectedRoom);
                    reservationRoom.setStatus("Reserved");
                    reservationRoom.setNote(notes);
                    reservationRooms.add(reservationRoom);
                }
            }

            // Đặt các phòng còn lại nếu quantity > 1
            int roomsToAdd = quantity - reservationRooms.size();
            for (int i = 0; i < roomsToAdd && i < availableRooms.size(); i++) {
                Room room = availableRooms.get(i);
                // Bỏ qua phòng đã được chọn
                if (roomId > 0 && room.getRoomId() == roomId) {
                    continue;
                }
                Reservation_Room reservationRoom = new Reservation_Room();
                reservationRoom.setReservation(reservation);
                reservationRoom.setRoom(room);
                reservationRoom.setStatus("Reserved");
                reservationRoom.setNote(notes);
                reservationRooms.add(reservationRoom);
            }

            for (Reservation_Room rr : reservationRooms) {
                reservationRoomRepository.save(rr);
            }

            return new BookingResult(reservation, roomType, days, totalAmount, reservationRooms);
        } catch (ParseException e) {
            throw new BookingException("Định dạng ngày không hợp lệ.");
        }
    }

    private void validateDateString(String dateStr, String message) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new BookingException(message);
        }
    }
}
