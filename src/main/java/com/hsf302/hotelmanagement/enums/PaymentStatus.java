package com.hsf302.hotelmanagement.enums;

/**
 * Enum cho các trạng thái thanh toán
 */
public enum PaymentStatus {
    PENDING("Pending", "Đang chờ thanh toán"),
    COMPLETED("Completed", "Đã thanh toán"),
    FAILED("Failed", "Thanh toán thất bại"),
    REFUNDED("Refunded", "Đã hoàn tiền"),
    PARTIALLY_PAID("Partially Paid", "Thanh toán một phần"),
    CANCELLED("Cancelled", "Đã hủy");
    
    private final String displayName;
    private final String vietnameseName;
    
    PaymentStatus(String displayName, String vietnameseName) {
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
