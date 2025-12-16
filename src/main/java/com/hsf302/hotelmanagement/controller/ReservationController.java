package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.Reservation;
import com.hsf302.hotelmanagement.entity.Reservation_Room;
import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    private record ReservationOverviewRecord(
            int reservationId,
            String guestName,
            String roomName,
            Date checkInDate,
            Date checkOutDate,
            int numberOfGuests,
            double totalAmount
    ){}

    @GetMapping("/manager/reservations-fragment")
    public String showReservationList(@RequestParam(value = "filter", required = false, defaultValue = "all") String filter, Model model){
        List<Reservation> reservations;
        if("today".equals(filter)){
            reservations = reservationService.getReservationForToday();
        } else if ("month".equals(filter)) {
            reservations = reservationService.getReservationForThisMonth();
        } else {
            reservations = reservationService.getAllReservations();
        }

        List<ReservationOverviewRecord> reservationList = new ArrayList<>();

        for(Reservation res : reservations){
            for(Reservation_Room reservationRoom : res.getReservation_rooms()){
                String guestName = res.getGuest().getFirstName() + " " + res.getGuest().getLastName();
                String roomName = reservationRoom.getRoom().getRoomName();
                double basePrice = reservationRoom.getRoom().getRoomType().getBasePrice();

                LocalDate in = res.getCheckInDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                LocalDate out = res.getCheckOutDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                long numberOfDays = ChronoUnit.DAYS.between(in, out);
                if(numberOfDays<=0) numberOfDays=1;
                double totalAmount = basePrice * numberOfDays;

                reservationList.add(new ReservationOverviewRecord(
                        res.getReservationId(),
                        guestName,
                        roomName,
                        res.getCheckInDate(),
                        res.getCheckOutDate(),
                        res.getNumberOfGuests(),
                        totalAmount
                ));
            }
        }

        model.addAttribute("resList", reservationList);
        model.addAttribute("filter", filter);

        return "manager/reservationList :: reservation-content";

    }
}
