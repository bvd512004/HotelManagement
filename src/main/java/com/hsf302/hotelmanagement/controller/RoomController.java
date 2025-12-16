package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.entity.Room_Status;
import com.hsf302.hotelmanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RoomController {

    @Autowired
    private RoomService roomService;

    // Main entry point for the manager dashboard
    @GetMapping("/manager/homeManager")
    public String managerHome() {
        return "manager/homeManager";
    }

    // --- Fragment Loading Endpoints (GET) ---

    @GetMapping("/manager/rooms-fragment")
    public String getRoomsFragment(Model model) {
        List<Room> rList = roomService.findAll();
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
        model.addAttribute("fno", room.getFloor().getFloor_number());
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
    public String deleteRoom(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        roomService.deleteRoom(id);
        redirectAttributes.addFlashAttribute("mess", "Delete successfully");
        return "redirect:/manager/homeManager";
    }

    // --- Kept original full-page endpoints for fallback ---
    @GetMapping("/viewRoomsByManager")
    public String viewRoomsByManager(Model model) {
        return getRoomsFragment(model);
    }
}
