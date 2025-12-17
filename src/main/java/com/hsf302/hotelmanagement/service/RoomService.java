
package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.dto.response.RoomDTO;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.entity.Room_Status;
import com.hsf302.hotelmanagement.entity.Floor;
import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    List<Room> findAll();
    Room findById(int id);
    void save(Room room);
    void delete(int id);
    List<RoomType> getAllRoomTypes();
    List<Room_Status> getAllRoomStatuses();
    List<Floor> getAllFloors();
    RoomType getRoomTypeById(int id);
    Room_Status getRoomStatusById(int id);
    Floor getFloorById(int id);
    List<RoomDTO> getRoomsWithStatus(LocalDate startDate, LocalDate endDate, String status);
}
