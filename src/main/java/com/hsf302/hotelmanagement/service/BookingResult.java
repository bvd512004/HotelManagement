package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Reservation;
import com.hsf302.hotelmanagement.entity.Reservation_Room;
import com.hsf302.hotelmanagement.entity.RoomType;

import java.util.List;

public class BookingResult {
    private final Reservation reservation;
    private final RoomType roomType;
    private final long days;
    private final double totalAmount;
    private final List<Reservation_Room> reservationRooms;

    public BookingResult(Reservation reservation, RoomType roomType, long days, double totalAmount, List<Reservation_Room> reservationRooms) {
        this.reservation = reservation;
        this.roomType = roomType;
        this.days = days;
        this.totalAmount = totalAmount;
        this.reservationRooms = reservationRooms;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public long getDays() {
        return days;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<Reservation_Room> getReservationRooms() {
        return reservationRooms;
    }
}

