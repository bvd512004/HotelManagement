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



    @GetMapping("/manager/rooms-fragment")
    public String getRoomsFragment(Model model) {
        List<Room> rList = roomService.findAllWithRelations();
        List<RoomType> rtList = roomService.findAllRoomTypes();
        model.addAttribute("rList", rList);
        model.addAttribute("rtList", rtList);
        return "manager/viewRoomsByManager :: room-list";
    }

    @GetMapping("/manager/add-room-type-fragment")
    public String getAddRoomTypeFragment(Model model) {
        return "manager/addRoomType :: add-room-type-form";
    }

    @GetMapping("/manager/add-room-fragment")
    public String getAddRoomFragment(Model model) {
        model.addAttribute("rtList", roomService.findAllRoomTypes());
        model.addAttribute("fList", roomService.findAllFloor());
        return "manager/addRoom :: add-room-form";
    }

    @GetMapping("/manager/edit-room-fragment")
    public String getEditRoomFragment(@RequestParam("id") int id, Model model) {
        Room room = roomService.findRoomById(id);
        model.addAttribute("room", room);
        model.addAttribute("rtList", roomService.findAllRoomTypes());
        model.addAttribute("rto", room.getRoomType().getTypeName());
        model.addAttribute("fno", room.getFloor().getFloorNumber());
        return "manager/editRoom :: edit-room-form";
    }

    @PostMapping("/manager/add-room-type")
    public String addRoomType(@RequestParam("name") String name,
                              @RequestParam("price") double price,
                              @RequestParam("capacity") int capacity,
                              @RequestParam("description") String description,
                              Model model) {
        if (roomService.checkDuplicate(name)) {
            model.addAttribute("mess", "Type name is already exist!");
        } else if (name.length() > 255) {
            model.addAttribute("mess", "Type name is too long!");
        } else if (price <= 0) {
            model.addAttribute("mess", "Invalid price!");
        } else if (capacity <= 0) {
            model.addAttribute("mess", "Invalid capacity!");
        } else if (description.length() > 255) {
            model.addAttribute("mess", "Description is too long!");
        } else {
            RoomType roomType = new RoomType(name, description, price, capacity);
            roomService.addNewRoomType(roomType);
            model.addAttribute("mess", "Add successfully!");
        }
        return getRoomsFragment(model);
    }

    @PostMapping("/manager/add-room")
    public String addRoom(@RequestParam("name") String name,
                          @RequestParam("type") String type,
                          @RequestParam("floor") String floor,
                          Model model) {
        if (roomService.checkDuplicate4Room(name)) {
            model.addAttribute("mess", "Room name is already exist!");
        } else if (name.length() > 8) {
            model.addAttribute("mess", "Room name is too long!");
        } else {
            int floorNumber = Integer.parseInt(floor);
            RoomType t = roomService.findTypeByName(type);
            Floor f = roomService.findFloorByNumber(floorNumber);
            Room_Status rs = roomService.findRoomStatusByName("Available");
            Room room = new Room(name, t, rs, f);
            roomService.addNewRoom(room);
            model.addAttribute("mess", "Add new room successfully!");
        }
        return getRoomsFragment(model);
    }

    @PostMapping("/manager/edit-room")
    public String editRoom(@RequestParam("id") int id,
                           @RequestParam("type") String type,
                           Model model) {
        RoomType rt = roomService.findTypeByName(type);
        roomService.updateRoom(id, rt);
        model.addAttribute("mess", "Save successfully!");
        return getRoomsFragment(model);
    }
    
    @PostMapping("/manager/search-room")
    public String searchRoomsByManager(@RequestParam("name") String name,
                                       @RequestParam("type") String type,
                                       Model model) {
        List<Room> rList;
        if (!name.isBlank() && !type.isBlank()) {
            rList = roomService.findByNameAndType(name, type);
        } else if (!name.isBlank()) {
            rList = roomService.findByName(name);
        } else {
            rList = roomService.findByType(type);
        }
        model.addAttribute("rList", rList);
        model.addAttribute("rtList", roomService.findAllRoomTypes());
        return "manager/viewRoomsByManager :: room-list";
    }

    // --- Delete Workflow ---

    @GetMapping("/manager/confirm-delete")
    public String confirmDelete(@RequestParam("id") int id, Model model) {
        Room room = roomService.findRoomById(id);
        model.addAttribute("room", room);
        return "manager/deleteConfirm";
    }

    @PostMapping("/manager/delete-room")
    public String deleteRoom(@RequestParam("id") int id, Model model) {
        roomService.deleteRoom(id);
        model.addAttribute("mess", "Delete successfully");
        return getRoomsFragment(model);
    }

    @GetMapping("/viewRoomsByManager")
    public String viewRoomsByManager(Model model) {
        return getRoomsFragment(model);
    }
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
        model.addAttribute("statusOptions", Arrays.asList("CheckedIn","CheckedOut", "Reserved", "Available"));

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