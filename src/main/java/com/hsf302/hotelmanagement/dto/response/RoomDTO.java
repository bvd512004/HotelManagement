package com.hsf302.hotelmanagement.dto.response;

import com.hsf302.hotelmanagement.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter



public class RoomDTO {
    private Room room;
    private String status;
    private Integer reservationId;

    public RoomDTO() {
    }

    public RoomDTO(Room room, String status, Integer reservationId) {
        this.room = room;
        this.status = status;
        this.reservationId = reservationId;
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

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }
}