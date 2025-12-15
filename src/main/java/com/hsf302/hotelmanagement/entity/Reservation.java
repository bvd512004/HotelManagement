package com.hsf302.hotelmanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ReservationId")
    private int reservationId;

    @Column(name="CheckInDate")
    private Date checkInDate;

    @Column(name="CheckOutDate")
    private Date checkOutDate;

    @Column(name="NumberOfGuests")
    private int numberOfGuests;

    @Column(name="TotalAmonut", nullable=false)
    private double totalAmount;

    @Column(name="Status", length=50)
    private String status;

    @CreationTimestamp
    @Column(name="CreatedAt", updatable=false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="GuestId", nullable=false)
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="UserId")
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="ReservationId")
    private List<Payment> payments;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation_Service> reservation_services;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="ReservationId")
    private List<Reservation_Room> reservation_rooms;

    public Reservation() {
    }

    public Reservation(Date checkInDate, Date checkOutDate, int numberOfGuests, double totalAmount, String status, LocalDateTime createdAt, Guest guest, User user, List<Payment> payments, List<Reservation_Service> reservation_services, List<Reservation_Room> reservation_rooms) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.guest = guest;
        this.user = user;
        this.payments = payments;
        this.reservation_services = reservation_services;
        this.reservation_rooms = reservation_rooms;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public List<Reservation_Service> getReservation_services() {
        return reservation_services;
    }

    public void setReservation_services(List<Reservation_Service> reservation_services) {
        this.reservation_services = reservation_services;
    }

    public List<Reservation_Room> getReservation_rooms() {
        return reservation_rooms;
    }

    public void setReservation_rooms(List<Reservation_Room> reservation_rooms) {
        this.reservation_rooms = reservation_rooms;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", numberOfGuests=" + numberOfGuests +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", guest=" + guest +
                ", user=" + user +
                ", payments=" + payments +
                ", reservation_services=" + reservation_services +
                ", reservation_rooms=" + reservation_rooms +
                '}';
    }
}
