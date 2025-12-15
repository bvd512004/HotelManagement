package com.hsf302.hotelmanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="payments")
public class Payment {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="PaymentId")
    private int paymentId;

    @Column(name="Amount", nullable=false)
    private double amount;

    @CreationTimestamp
    @Column(name="PaymentDate", updatable=false)
    private LocalDateTime paymentDate;

    @Column(name="PaymentMethod", length=50)
    private String paymentMethod;

    @Column(name="Status", length=50)
    private String status;

    @ManyToOne
    @JoinColumn(name="ReservationId", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name="GuestGuestId", nullable=false)
    private Guest guest;

    public Payment() {
    }

    public Payment(double amount, LocalDateTime paymentDate, String paymentMethod, String status, Reservation reservation, Guest guest) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.reservation = reservation;
        this.guest = guest;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    @Transient
    public int getId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", amount=" + amount +
                ", paymentDate=" + paymentDate +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", reservation=" + reservation +
                ", guest=" + guest +
                '}';
    }
}
