package com.hsf302.hotelmanagement.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="room_statuss")
public class Room_Status {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="RoomStatusId")
    private int roomStatusId;

    @Column(name="RoomStatus", length=50)
    private String roomStatus;

    @OneToMany(mappedBy = "roomStatus")
    private List<Room> rooms;

    public Room_Status() {
    }

    public Room_Status(String roomStatus, List<Room> rooms) {
        this.roomStatus = roomStatus;
        this.rooms = rooms;
    }

    public int getRoomStatusId() {
        return roomStatusId;
    }

    public void setRoomStatusId(int roomStatusId) {
        this.roomStatusId = roomStatusId;
    }

    @Transient
    public int getId() {
        return roomStatusId;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "Room_Status{" +
                "roomStatusId=" + roomStatusId +
                ", roomStatus='" + roomStatus + '\'' +
                ", rooms=" + rooms +
                '}';
    }
}
