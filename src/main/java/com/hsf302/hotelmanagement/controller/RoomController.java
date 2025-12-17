package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.dto.response.RoomDTO;
import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.entity.Room_Status;
import com.hsf302.hotelmanagement.repository.RoomTypeRepository;
import com.hsf302.hotelmanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Arrays;
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
    public String listRooms(@RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                            @RequestParam(name = "status", required = false) String status,
                            Model model) {

        // Default to today if dates are not provided
        LocalDate filterStartDate = (startDate == null) ? LocalDate.now() : startDate;
        LocalDate filterEndDate = (endDate == null) ? filterStartDate : endDate;


        List<RoomDTO> roomDTOs = roomService.getRoomsWithStatus(filterStartDate, filterEndDate, status);

        // Group rooms by floor
        Map<Floor, List<RoomDTO>> floorRooms = new LinkedHashMap<>();
        roomDTOs.stream()
                .filter(r -> r.getRoom().getRoomStatus() != null && "Available".equals(r.getRoom().getRoomStatus().getRoomStatus()))
                .sorted((r1, r2) -> {
                    if (r1.getRoom().getFloor() == null && r2.getRoom().getFloor() == null) return 0;
                    if (r1.getRoom().getFloor() == null) return 1;
                    if (r2.getRoom().getFloor() == null) return -1;
                    return Integer.compare(r1.getRoom().getFloor().getFloorId(), r2.getRoom().getFloor().getFloorId());
                })
                .forEach(roomDTO -> {
                    Floor floor = roomDTO.getRoom().getFloor();
                    floorRooms.computeIfAbsent(floor, k -> new java.util.ArrayList<>()).add(roomDTO);
                });

        model.addAttribute("floorRooms", floorRooms);
        model.addAttribute("startDate", filterStartDate);
        model.addAttribute("endDate", filterEndDate);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statusOptions", Arrays.asList("Booked", "Reserved", "Available"));

        return "room-list";
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