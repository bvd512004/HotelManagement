package com.hsf302.hotelmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name="reservation_rooms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ReservationId","RoomId"})
)
public class Reservation_Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReservationId")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoomId")
    private Room room;

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "Note")
    private String note;

    public Reservation_Room() {
    }

    public Reservation_Room(Reservation reservation, Room room, String status, String note) {
        this.reservation = reservation;
        this.room = room;
        this.status = status;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Reservation_Room{" +
                "id=" + id +
                ", reservation=" + reservation +
                ", room=" + room +
                ", status='" + status + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
