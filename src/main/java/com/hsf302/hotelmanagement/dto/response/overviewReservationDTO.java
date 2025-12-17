package com.hsf302.hotelmanagement.dto.response;

import java.util.Date;

public class overviewReservationDTO {
    private int reservationId;
    private String guestName;
    private String roomName;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfGuests;
    private double totalAmount;

    public overviewReservationDTO(int reservationId, String guestName, String roomName, Date checkInDate, Date checkOutDate, int numberOfGuests, double totalAmount) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomName = roomName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.totalAmount = totalAmount;
    }

    // Getters
    public int getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomName() { return roomName; }
    public Date getCheckInDate() { return checkInDate; }
    public Date getCheckOutDate() { return checkOutDate; }
    public int getNumberOfGuests() { return numberOfGuests; }
    public double getTotalAmount() { return totalAmount; }
}
