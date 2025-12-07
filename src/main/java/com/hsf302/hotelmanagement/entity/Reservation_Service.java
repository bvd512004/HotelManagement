package com.hsf302.hotelmanagement.entity;


import jakarta.persistence.*;

@Entity
@Table(name="reservation_services",
        uniqueConstraints = @UniqueConstraint(columnNames = {"Reservation_Id","Service_Id"})
)
public class Reservation_Service {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="Id")
    private int reservation_serviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Reservation_Id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Service_Id", nullable = false)
    private Service service;

    @Column(name="Quantity")
    private int quantity;

    @Column(name="PriceAtTheTime")
    private double priceAtTheTime;

    public Reservation_Service() {
    }

    public Reservation_Service(Reservation reservation, Service service, int quantity, double priceAtTheTime) {
        this.reservation = reservation;
        this.service = service;
        this.quantity = quantity;
        this.priceAtTheTime = priceAtTheTime;
    }

    public int getReservation_serviceId() {
        return reservation_serviceId;
    }

    public void setReservation_serviceId(int reservation_serviceId) {
        this.reservation_serviceId = reservation_serviceId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtTheTime() {
        return priceAtTheTime;
    }

    public void setPriceAtTheTime(double priceAtTheTime) {
        this.priceAtTheTime = priceAtTheTime;
    }

    @Override
    public String toString() {
        return "Reservation_Service{" +
                "reservation_serviceId=" + reservation_serviceId +
                ", reservation=" + reservation +
                ", serviceId=" + service +
                ", quantity=" + quantity +
                ", priceAtTheTime=" + priceAtTheTime +
                '}';
    }
}
