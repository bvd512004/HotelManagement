package com.hsf302.hotelmanagement.enums;

/**
 * Enum cho các loại thanh toán
 */
public enum PaymentType {
    RESERVATION("Reservation", "Thanh toán đặt phòng"),
    DEPOSIT("Deposit", "Thanh toán đặt cọc"),
    FULL_PAYMENT("Full Payment", "Thanh toán toàn bộ"),
    ADDITIONAL_SERVICE("Additional Service", "Thanh toán dịch vụ thêm"),
    DAMAGE_FEE("Damage Fee", "Phí bồi thường"),
    REFUND("Refund", "Hoàn tiền");
    
    private final String displayName;
    private final String vietnameseName;
    
    PaymentType(String displayName, String vietnameseName) {
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
