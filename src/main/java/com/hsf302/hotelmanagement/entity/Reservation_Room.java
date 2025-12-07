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
    private Reservation reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoomId")
    private Room room;

    @Column(name = "Status", length = 50)
    private String Status;

    @Column(name = "Note")
    private String Note;

    public Reservation_Room() {
    }

    public Reservation_Room(Reservation reservationId, Room room, String status, String note) {
        this.reservationId = reservationId;
        this.room = room;
        Status = status;
        Note = note;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Reservation getReservationId() {
        return reservationId;
    }

    public void setReservationId(Reservation reservationId) {
        this.reservationId = reservationId;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Reservation_Room{" +
                "id=" + id +
                ", reservationId=" + reservationId +
                ", room=" + room +
                ", Status='" + Status + '\'' +
                ", Note='" + Note + '\'' +
                '}';
    }
}
