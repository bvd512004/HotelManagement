package com.hsf302.hotelmanagement.entity;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name="rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="RoomId")
    private int roomId;

    @Column(name="RoomName", columnDefinition = "NVARCHAR(50)")
    private String roomName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="RoomTypeId")
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="RoomStatusId")
    private Room_Status roomStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="FloorId")
    private Floor floor;

    @OneToMany(mappedBy = "room")
    private List<Reservation_Room> reservation_rooms;

    public Room() {
    }

    public Room(String roomName, RoomType roomType, Room_Status roomStatus, Floor floor, List<Reservation_Room> reservation_rooms) {
        this.roomName = roomName;
        this.roomType = roomType;
        this.roomStatus = roomStatus;
        this.floor = floor;
        this.reservation_rooms = reservation_rooms;
    }

    public Room(String name, RoomType t, Room_Status rs, Floor f) {
        this.roomName = name;
        this.roomType = t;
        this.roomStatus = rs;
        this.floor = f;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @Transient
    public int getId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Room_Status getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(Room_Status roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public List<Reservation_Room> getReservation_rooms() {
        return reservation_rooms;
    }

    public void setReservation_rooms(List<Reservation_Room> reservation_rooms) {
        this.reservation_rooms = reservation_rooms;
    }

}