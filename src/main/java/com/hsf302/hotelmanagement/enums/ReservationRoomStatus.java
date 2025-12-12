package com.hsf302.hotelmanagement.enums;

/**
 * Enum cho các trạng thái của phòng trong đặt phòng (ReservationRoom)
 */
public enum ReservationRoomStatus {
    CONFIRMED("Confirmed", "Đã xác nhận"),
    CHECKED_IN("Checked In", "Đã nhận phòng"),
    CHECKED_OUT("Checked Out", "Đã trả phòng"),
    CANCELLED("Cancelled", "Đã hủy"),
    CHANGED("Changed", "Đã đổi phòng");
    
    private final String displayName;
    private final String vietnameseName;
    
    ReservationRoomStatus(String displayName, String vietnameseName) {
        this.displayName = displayName;
        this.vietnameseName = vietnameseName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getVietnameseName() {
        return vietnameseName;
    }
}
