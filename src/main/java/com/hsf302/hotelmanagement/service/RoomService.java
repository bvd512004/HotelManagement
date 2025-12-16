package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.entity.Room_Status;

import java.util.List;

public interface RoomService {
    public List<Room> findAll();
    public List<Room> findByName(String name);
    public List<Room> findByType(String type);
    public List<Room> findByNameAndType(String name, String type);
    public Room findRoomById(int id);
    public List<RoomType> findAllRoomTypes();
    public List<Floor> findAllFloor();
    public RoomType findTypeByName(String typeName);
    public Floor findFloorByNumber(int number);
    public Room_Status findRoomStatusByName(String status);
    public boolean checkDuplicate(String name);
    public boolean checkDuplicate4Room(String name);
    public void addNewRoomType(RoomType roomType);
    public void addNewRoom(Room room);
    public void deleteRoom(int id);
    public void updateRoom(int id, RoomType roomType);



}
