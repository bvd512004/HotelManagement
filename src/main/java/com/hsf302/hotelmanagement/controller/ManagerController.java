package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/homeManager")
    public String managerHome() {
        return "manager/homeManager";
    }

    @GetMapping("/overview-fragment")
    public String getOverviewFragment(@RequestParam(value = "filter", required = false, defaultValue = "all") String filter, Model model) {
        double totalRoomAmount = reservationService.calculateTotalRoomAmount(filter);
        double totalServiceAmount = reservationService.calculateTotalServiceAmount(filter);
        double totalIncome = reservationService.calculateTotalIncome(filter);

        model.addAttribute("totalRoomAmount", totalRoomAmount);
        model.addAttribute("totalServiceAmount", totalServiceAmount);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("filter", filter); // Add filter to model for active button styling

        return "manager/overview :: overview-content";
    }
}
