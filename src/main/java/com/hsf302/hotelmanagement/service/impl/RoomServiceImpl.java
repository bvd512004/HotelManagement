
package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.entity.Room_Status;
import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.repository.RoomRepository;
import com.hsf302.hotelmanagement.repository.RoomTypeRepository;
import com.hsf302.hotelmanagement.repository.RoomStatusRepository;
import com.hsf302.hotelmanagement.repository.FloorRepository;
import com.hsf302.hotelmanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
