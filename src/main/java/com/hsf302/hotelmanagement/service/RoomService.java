package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;

import java.util.List;

public interface RoomService {
    public List<Room> findAll();
    public List<Room> findByName(String name);
    public List<Room> findByType(String type);
    public List<Room> findByNameAndType(String name, String type);
    List<RoomType> findAllRoomTypes();
    List<Floor> findAllFloor();
    public void addNewRoom(Room room);

}
