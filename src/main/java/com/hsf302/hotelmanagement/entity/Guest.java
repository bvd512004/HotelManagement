package com.hsf302.hotelmanagement.entity;

import jakarta.persistence.*;


import java.util.List;

@Entity
@Table(name="guests")
public class Guest {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="GuestId")
    private int guestId;

    @Column(name="FirstName",columnDefinition = "Nvarchar(255)")
    private String firstName;

    @Column(name="LastName",columnDefinition = "Nvarchar(255)")
    private String lastName;

    @Column(name="Email", unique=true, nullable=false)
    private String email;

    @Column(name="PhoneNumber", length=10, nullable=false)
    private String phoneNumber;

    @Column(name="Address",columnDefinition = "Nvarchar(255)")
    private String address;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
    private List<Payment> payments ;

    public Guest() {
    }

    public Guest(String firstName, String address, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.address = address;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
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

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    @Transient
    public int getId() {
        return guestId;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "guestId=" + guestId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", reservations=" + reservations +
                ", payments=" + payments +
                '}';
    }
}