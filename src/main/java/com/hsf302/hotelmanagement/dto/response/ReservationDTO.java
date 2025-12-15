package com.hsf302.hotelmanagement.dto.response;

import java.util.List;

public class ReservationDTO {
    private int roomId;
    private int roomTypeId;
    private String roomName;
    private String typeName;
    private String checkInDate;
    private String checkOutDate;
    private double roomPrice;
    private int numberOfGuests;

    // Guest info
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private int guestId;

    // Services
    private List<Integer> serviceIds;
    private List<Integer> serviceQuantities;

    // Total calculation
    private double totalRoomPrice;
    private double totalServicePrice;
    private double grandTotal;

    public ReservationDTO() {
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getRoomPrice() {
        return roomPrice;
    }

    public void setRoomPrice(double roomPrice) {
        this.roomPrice = roomPrice;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<Integer> getServiceQuantities() {
        return serviceQuantities;
    }

    public void setServiceQuantities(List<Integer> serviceQuantities) {
        this.serviceQuantities = serviceQuantities;
    }

    public double getTotalRoomPrice() {
        return totalRoomPrice;
    }

    public void setTotalRoomPrice(double totalRoomPrice) {
        this.totalRoomPrice = totalRoomPrice;
    }

    public double getTotalServicePrice() {
        return totalServicePrice;
    }

    public void setTotalServicePrice(double totalServicePrice) {
        this.totalServicePrice = totalServicePrice;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }
}

