package com.hsf302.hotelmanagement.enums;

/**
 * Enum cho các trạng thái của đặt phòng
 */
public enum ReservationStatus {
    PENDING("Pending", "Đang chờ xác nhận"),
    CONFIRMED("Confirmed", "Đã xác nhận"),
    CHECKED_IN("Checked In", "Đã check-in"),
    CHECKED_OUT("Checked Out", "Đã check-out"),
    CANCELLED("Cancelled", "Đã hủy"),
    NO_SHOW("No Show", "Không đến");
    
    private final String displayName;
    private final String vietnameseName;
    
    ReservationStatus(String displayName, String vietnameseName) {
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
