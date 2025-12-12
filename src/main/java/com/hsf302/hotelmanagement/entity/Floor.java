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
    private int floor_number;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="FloorId")
    private List<Room> rooms;

    public Floor() {
    }

    public Floor(int floor_number, List<Room> rooms) {
        this.floor_number = floor_number;
        this.rooms = rooms;
    }

    public Floor(int floor_number) {
        this.floor_number = floor_number;
    }

    public int getFloorId() {
        return floorId;
    }

    public void setFloorId(int floorId) {
        this.floorId = floorId;
    }

    public int getFloor_number() {
        return floor_number;
    }

    public void setFloor_number(int floor_number) {
        this.floor_number = floor_number;
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
                ", floor_number=" + floor_number +
                ", rooms=" + rooms +
                '}';
    }
}
