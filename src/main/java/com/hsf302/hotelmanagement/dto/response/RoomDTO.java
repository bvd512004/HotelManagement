package com.hsf302.hotelmanagement.dto.response;

import com.hsf302.hotelmanagement.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Room room;
    private String status; // e.g., "Booked", "Checked-in","Reserved"
    private Integer reservationId;
}