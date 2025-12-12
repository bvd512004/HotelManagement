package com.hsf302.hotelmanagement.enums;

/**
 * Enum cho các phương thức thanh toán
 */
public enum PaymentMethod {
    CASH("Cash", "Tiền mặt"),
    CREDIT_CARD("Credit Card", "Thẻ tín dụng"),
    DEBIT_CARD("Debit Card", "Thẻ ghi nợ"),
    BANK_TRANSFER("Bank Transfer", "Chuyển khoản"),
    E_WALLET("E-Wallet", "Ví điện tử"),
    MOMO("MoMo", "Ví MoMo"),
    VNPAY("VNPay", "VNPay"),
    ZALOPAY("ZaloPay", "ZaloPay");
    
    private final String displayName;
    private final String vietnameseName;
    
    PaymentMethod(String displayName, String vietnameseName) {
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
