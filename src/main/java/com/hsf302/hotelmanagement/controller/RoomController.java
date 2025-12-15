package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.entity.Room_Status;
import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.service.RoomService;
import com.hsf302.hotelmanagement.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;
  
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @GetMapping("/rooms")
    public String rooms(Model model) {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        model.addAttribute("roomTypes", roomTypes);
        return "rooms";
    }

    @GetMapping("/gallery")
    public String gallery() {
        return "gallery";
    }
    @GetMapping("/list")
    public String listRooms(Model model) {
        List<Room> rooms = roomService.findAll();

        // Group rooms by floor
        Map<Floor, List<Room>> floorRooms = new LinkedHashMap<>();

        // Sort rooms by floor number and group them
        rooms.stream()
                .sorted((r1, r2) -> {
                    if (r1.getFloor() == null && r2.getFloor() == null) return 0;
                    if (r1.getFloor() == null) return 1;
                    if (r2.getFloor() == null) return -1;
                    return Integer.compare(r1.getFloor().getFloorId(), r2.getFloor().getFloorId());
                })
                .forEach(room -> {
                    Floor floor = room.getFloor();
                    floorRooms.computeIfAbsent(floor, k -> new java.util.ArrayList<>()).add(room);
                });

        model.addAttribute("rooms", rooms);
        model.addAttribute("floorRooms", floorRooms);
        model.addAttribute("view", "room-list");

        // Return the main layout
        return "dashboard-layout";
    }

    @GetMapping("/create")
    public String showCreateRoomForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("roomTypes", roomService.getAllRoomTypes());
        model.addAttribute("roomStatuses", roomService.getAllRoomStatuses());
        model.addAttribute("floors", roomService.getAllFloors());
        return "room-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditRoomForm(@PathVariable("id") int id, Model model) {
        Room room = roomService.findById(id);
        model.addAttribute("room", room);
        model.addAttribute("roomTypes", roomService.getAllRoomTypes());
        model.addAttribute("roomStatuses", roomService.getAllRoomStatuses());
        model.addAttribute("floors", roomService.getAllFloors());
        return "room-form";
    }

    @PostMapping("/save")
    public String saveRoom(@ModelAttribute("room") Room room,
                          @RequestParam(required = false) Integer roomTypeId,
                          @RequestParam(required = false) Integer roomStatusId,
                          @RequestParam(required = false) Integer floorId) {

        // Fetch and set the related entities from database
        if (roomTypeId != null) {
            RoomType roomType = roomService.getRoomTypeById(roomTypeId);
            if (roomType != null) {
                room.setRoomType(roomType);
            }
        }

        if (roomStatusId != null) {
            Room_Status roomStatus = roomService.getRoomStatusById(roomStatusId);
            if (roomStatus != null) {
                room.setRoomStatus(roomStatus);
            }
        }

        if (floorId != null && floorId > 0) {
            Floor floor = roomService.getFloorById(floorId);
            if (floor != null) {
                room.setFloor(floor);
            }
        }

        roomService.save(room);
        return "redirect:/rooms/list";
    }

}

