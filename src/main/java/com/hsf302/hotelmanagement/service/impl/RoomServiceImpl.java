package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.repository.RoomRepository;
import com.hsf302.hotelmanagement.repository.RoomTypeRepository;
import com.hsf302.hotelmanagement.repository.FloorRepository;
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

    private FloorRepository floorRepository;

    @Override
    public List<Room> findAll() {
        List<Room> rList =new ArrayList<>();
        for(Room room : rList){
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
    public void addNewRoom(Room room) {
        roomRepository.save(room);
    }

    @Override
    public List<Floor> findAllFloor() {
        return  floorRepository.findAll();
    }

    @Override
    public List<RoomType> findAllRoomTypes() {
        return roomTypeRepository.findAll();
    }
}
