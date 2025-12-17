package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.dto.response.RoomDTO;
import com.hsf302.hotelmanagement.entity.*;
import com.hsf302.hotelmanagement.repository.*;
import com.hsf302.hotelmanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomStatusRepository roomStatusRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private ReservationRoomRepository reservationRoomRepository;

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room findById(int id) {
        return roomRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Room room) {
        roomRepository.save(room);
    }

    @Override
    public void delete(int id) {
        roomRepository.deleteById(id);
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    @Override
    public List<Room_Status> getAllRoomStatuses() {
        return roomStatusRepository.findAll();
    }

    @Override
    public List<Floor> getAllFloors() {
        return floorRepository.findAll();
    }

    @Override
    public RoomType getRoomTypeById(int id) {
        return roomTypeRepository.findById(id).orElse(null);
    }

    @Override
    public Room_Status getRoomStatusById(int id) {
        return roomStatusRepository.findById(id).orElse(null);
    }

    @Override
    public Floor getFloorById(int id) {
        return floorRepository.findById(id).orElse(null);
    }

    @Override
    public List<RoomDTO> getRoomsWithStatus(LocalDate startDate, LocalDate endDate, String status) {
        List<Room> rooms = roomRepository.findAll();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        return rooms.stream()
                .map(room -> {
                    List<Reservation_Room> reservationRooms = reservationRoomRepository
                            .findReservationByRoomAndDateRange(room.getRoomId(), startDateTime, endDateTime);

                    String currentStatus = "Available";
                    Integer reservationId = null;
                    if (!reservationRooms.isEmpty()) {
                        // If there are any reservations in the range, the room is not available.
                        // We can just take the status from the first one for simplicity.
                        Reservation_Room rr = reservationRooms.get(0);
                        currentStatus = rr.getStatus(); // "Booked", "Reserved"
                        reservationId = rr.getReservation().getReservationId();
                    }

                    return new RoomDTO(room, currentStatus, reservationId);
                })
                .filter(dto -> status == null || status.isEmpty() || dto.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
}