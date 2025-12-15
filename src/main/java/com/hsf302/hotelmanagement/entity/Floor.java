package com.hsf302.hotelmanagement.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="floors")
public class Floor {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="FloorId")
    private int floorId;

    @Column(name="Floor_Number")
    private int floorNumber;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="FloorId")
    private List<Room> rooms;

    public Floor() {
    }

    public Floor(int floorNumber, List<Room> rooms) {
        this.floorNumber = floorNumber;
        this.rooms = rooms;
    }

    public int getFloorId() {
        return floorId;
    }

    public void setFloorId(int floorId) {
        this.floorId = floorId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "Floor{" +
                "floorId=" + floorId +
                ", floorNumber=" + floorNumber +
                ", rooms=" + rooms +
                '}';
    }
}
