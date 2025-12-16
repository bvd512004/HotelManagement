package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.entity.Room_Status;
import com.hsf302.hotelmanagement.repository.RoomRepository;
import com.hsf302.hotelmanagement.repository.RoomTypeRepository;
import com.hsf302.hotelmanagement.repository.FloorRepository;
import com.hsf302.hotelmanagement.repository.Room_StatusRepository;
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
    public List<Room> findAll() {
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
            if(f.getFloor_number() == number){
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
}
