package com.hsf302.hotelmanagement.enums;

/**
 * Enum cho các vai trò người dùng trong hệ thống
 */
public enum UserRole {
    ADMIN("ADMIN",1L),
    HOTEL_MANAGER("HOTEL_MANAGER", 2L),
    RECEPTIONIST("RECEPTIONIST", 3L),
    HOUSEKEEPING("HOUSEKEEPING", 4L),
    CUSTOMER("CUSTOMER", 5L);
    
    private final String roleName;
    private final Long roleId;

    UserRole(String roleName, Long roleId) {
        this.roleName = roleName;
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public Long getRoleId() {
        return roleId;
    }
}
