package com.hsf302.hotelmanagement.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="UserId")
    private int userId;

    @Column(name="UserName", unique=true, columnDefinition = "NVARCHAR(MAX)")
    private String userName;

    @Column(name="Password", columnDefinition = "NVARCHAR(MAX)")
    private String password;

    @Column(name="Role", nullable=false, length=20, columnDefinition = "NVARCHAR(20)")
    private String role;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="UserId")
    private List<Reservation> reservations;

    public User() {
    }

    public User(String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", reservations=" + reservations +
                '}';
    }
}
