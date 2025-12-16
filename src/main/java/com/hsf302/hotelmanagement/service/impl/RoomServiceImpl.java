package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.entity.Room_Status;
import com.hsf302.hotelmanagement.repository.*;
import com.hsf302.hotelmanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private Room_StatusRepository roomStatusRepository;

    @Override
    public List<Room> findAllWithRelations() {
        List<Room> rList =new ArrayList<>();
        for(Room room : roomRepository.findAllWithRelations()){
            rList.add(room);
        }
        return rList;
    }

    @Override
    public List<Room> findByName(String name) {
        List<Room> rList = new ArrayList<>();
        for(Room room : roomRepository.findAll()){
            if(room.getRoomName().contains(name)){
                rList.add(room);
            }
        }
        return rList;
    }

    @Override
    public List<Room> findByType(String type) {
        List<Room> rList = new ArrayList<>();
        for(Room room : roomRepository.findAll()){
            if(room.getRoomType().getTypeName().equals(type)){
                rList.add(room);
            }
        }
        return rList;
    }

    @Override
    public List<Room> findByNameAndType(String name, String type) {
        List<Room> rList = new ArrayList<>();
        for(Room room : roomRepository.findAll()){
            if(room.getRoomType().getTypeName().equals(type) && room.getRoomName().contains(name)){
                rList.add(room);
            }
        }
        return rList;
    }

    @Override
    public Room findRoomById(int id) {
        return roomRepository.findById(id).get();
    }

    @Override
    public boolean checkDuplicate(String name) {
        for(RoomType roomType : roomTypeRepository.findAll()) {
            if (roomType.getTypeName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkDuplicate4Room(String name) {
        for(Room room : roomRepository.findAll()) {
            if (room.getRoomName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addNewRoomType(RoomType roomType) {roomTypeRepository.save(roomType);}

    @Override
    public void addNewRoom(Room room) {roomRepository.save(room);}

    @Override
    public List<Floor> findAllFloor() {return  floorRepository.findAll(); }

    @Override
    public List<RoomType> findAllRoomTypes() {return roomTypeRepository.findAll();}

    @Override
    public Floor findFloorByNumber(int number) {
        for(Floor f : floorRepository.findAll()){
            if(f.getFloorNumber() == number){
                return f;
            }
        }
        return null;
    }

    @Override
    public RoomType findTypeByName(String typeName) {
        for(RoomType rt : roomTypeRepository.findAll()){
            if(rt.getTypeName().equals(typeName)){
                return rt;
            }
        }
        return null;
    }

    @Override
    public Room_Status findRoomStatusByName(String status) {
        for(Room_Status rs : roomStatusRepository.findAll()){
            if(rs.getRoomStatus().equals(status)){
                return rs;
            }
        }
        return null;
    }

    @Override
    public void deleteRoom(int id) {roomRepository.deleteById(id);}

    @Override
    public void updateRoom(int id, RoomType roomType) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        room.setRoomType(roomType);
        roomRepository.save(room);
    }

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
}
