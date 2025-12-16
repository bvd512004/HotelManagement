package com.hsf302.hotelmanagement.entity;
import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name="roomtypes")
public class RoomType {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="RoomTypeId")
    private int roomTypeId;

    @Column(name="TypeName", columnDefinition = "NVARCHAR(MAX)")
    private String typeName;

    @Column(name="Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name="BasePrice", nullable=false)
    private double basePrice;

    @Column(name="Capacity")
    private int capacity;

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;

    public RoomType() {
    }

    public RoomType(String typeName, String description, double basePrice, int capacity, List<Room> rooms) {
        this.typeName = typeName;
        this.description = description;
        this.basePrice = basePrice;
        this.capacity = capacity;
        this.rooms = rooms;
    }

    public RoomType(String typeName, String description, double basePrice, int capacity) {
        this.typeName = typeName;
        this.description = description;
        this.basePrice = basePrice;
        this.capacity = capacity;
    }

    public RoomType(String typeName) {
        this.typeName = typeName;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @Transient
    public int getId() {
        return roomTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return "RoomType{" +
                "roomTypeId=" + roomTypeId +
                ", typeName='" + typeName + '\'' +
                ", description='" + description + '\'' +
                ", basePrice=" + basePrice +
                ", capacity=" + capacity +
                ", rooms=" + rooms +
                '}';
    }
}
