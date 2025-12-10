package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.RoomType;
import com.hsf302.hotelmanagement.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class RoomController {

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
}

