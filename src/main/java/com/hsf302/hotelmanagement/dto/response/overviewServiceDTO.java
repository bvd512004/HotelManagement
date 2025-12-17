package com.hsf302.hotelmanagement.dto.response;

public class overviewServiceDTO {
    private int reservationId;
    private String serviceName;
    private String category;
    private String status;
    private int quantity;
    private double price;

    public overviewServiceDTO(int reservationId, String serviceName, String category, String status, int quantity, double price) {
        this.reservationId = reservationId;
        this.serviceName = serviceName;
        this.category = category;
        this.status = status;
        this.quantity = quantity;
        this.price = price;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
