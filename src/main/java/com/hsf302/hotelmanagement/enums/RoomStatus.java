package com.hsf302.hotelmanagement.enums;

/**
 * Enum cho các trạng thái của phòng
 */
public enum RoomStatus {
    AVAILABLE("Available", "Phòng trống"),
    OCCUPIED("Occupied", "Đang sử dụng"),
    RESERVED("Reserved", "Đã đặt"),
    CLEANING("Cleaning", "Đang dọn dẹp"),
    MAINTENANCE("Maintenance", "Đang bảo trì"),
    OUT_OF_ORDER("Out of Order", "Hỏng hóc");
    
    private final String displayName;
    private final String vietnameseName;
    
    RoomStatus(String displayName, String vietnameseName) {
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
