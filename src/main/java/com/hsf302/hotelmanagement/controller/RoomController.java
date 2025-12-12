package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.Floor;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RoomController {
    @Autowired
    private RoomService roomService;

@GetMapping("/viewRoomsByManager")
    public String viewRoomsByManager(Model model){
        List<Room> rList = roomService.findAll();
        List<RoomType> rtList = roomService.findAllRoomTypes();
        model.addAttribute("rList", rList);
        model.addAttribute("rtList", rtList);
        return "viewRoomsByManager";
}

@PostMapping("/searchRoomByManager")
    public String searchRoomsByManager(@RequestParam("name") String name,
                                       @RequestParam("type") String type,
                                       Model model){
    List<Room> rList = new ArrayList<>();
    if(!name.isBlank() && !name.isEmpty() && !type.isEmpty() && !type.isBlank()){
        rList = roomService.findByNameAndType(name, type);
    }
    else if(!name.isBlank() && !name.isEmpty()){
        rList = roomService.findByName(name);
    }else{
        rList = roomService.findByType(type);
    }
        model.addAttribute("rList", rList);
        return "viewRoomsByManager";
    }

    @GetMapping("/showAddRoom")
    public String showAddRoomPage(Model model){
        List<RoomType> rtList = roomService.findAllRoomTypes();
        List<Floor> fList = roomService.findAllFloor();
        model.addAttribute("rtList", rtList);
        model.addAttribute("fList", fList);
        return "addMoreRoomByManager";
    }

    @PostMapping("/addRoom")
    public String addNewRoom(@RequestParam("name")String name,
                             @RequestParam("type")String type,
                             @RequestParam("floor")int floor,
                             Model model){
        RoomType rt = new RoomType(type);
        Floor f = new Floor(floor);
        Room r = new Room(name, rt, f);
        roomService.addNewRoom(r);
        return "viewRoomsByManager";
    }
}
