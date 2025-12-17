
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
    List<Room> findAllWithRelations();
    List<RoomType> findAllRoomTypes();
    List<Room> findByName(String name);
    List<Room> findByType(String type);
    List<Room> findByNameAndType(String name, String type);
    Room findRoomById(int id);
    boolean checkDuplicate(String name);
    public boolean checkDuplicate4Room(String name);
    public void addNewRoomType(RoomType roomType);
    public void addNewRoom(Room room);
    public List<Floor> findAllFloor();
    public Floor findFloorByNumber(int number);
    RoomType findTypeByName(String typeName);
    Room_Status findRoomStatusByName(String status);
    void deleteRoom(int id);
    void updateRoom(int id, RoomType roomType);
}
