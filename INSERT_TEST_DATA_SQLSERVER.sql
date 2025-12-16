-- ============================================
-- SQL SERVER SCRIPT - CHÈN DỮ LIỆU TEST CHECK-IN
-- ============================================
-- Database: HotelManagement
-- Date: December 15, 2025
-- Purpose: Insert test data for Check-in feature

-- ============================================
-- 1. KIỂM TRA VÀ XOÁ DỮ LIỆU CŨ (OPTIONAL)
-- ============================================
-- Nếu muốn xoá dữ liệu cũ, hãy bỏ comment các dòng dưới
/*
DELETE FROM reservation_services WHERE Reservation_Id IN (SELECT ReservationId FROM reservations WHERE GuestId IN (SELECT GuestId FROM guests WHERE Email LIKE '%test%' OR Email LIKE '%example%'));
DELETE FROM reservation_rooms WHERE ReservationId IN (SELECT ReservationId FROM reservations WHERE GuestId IN (SELECT GuestId FROM guests WHERE Email LIKE '%test%' OR Email LIKE '%example%'));
DELETE FROM payments WHERE GuestGuestId IN (SELECT GuestId FROM guests WHERE Email LIKE '%test%' OR Email LIKE '%example%');
DELETE FROM reservations WHERE GuestId IN (SELECT GuestId FROM guests WHERE Email LIKE '%test%' OR Email LIKE '%example%');
DELETE FROM guests WHERE Email LIKE '%test%' OR Email LIKE '%example%';
*/

-- ============================================
-- 2. THÊM KHÁCH HÀNG (GUESTS)
-- ============================================

INSERT INTO guests (FirstName, LastName, Email, PhoneNumber, Address)
VALUES
    (N'Nguyễn', N'Văn A', N'nguyenvana@email.com', N'0912345678', N'123 Đường Lê Lợi, Hà Nội'),
    (N'Trần', N'Thị B', N'tranthib@email.com', N'0987654321', N'456 Đường Trần Hưng Đạo, TP. HCM'),
    (N'Lê', N'Minh C', N'leminhc@email.com', N'0901234567', N'789 Đường Hùng Vương, Đà Nẵng'),
    (N'Phạm', N'Quốc D', N'phamquocd@email.com', N'0908765432', N'321 Đường Nguyễn Huệ, Hà Nội'),
    (N'Vũ', N'Hương E', N'vuhuange@email.com', N'0934567890', N'654 Đường Pasteur, TP. HCM'),
    (N'Đặng', N'Minh F', N'dangminhf@email.com', N'0945678901', N'987 Đường Phan Đình Phùng, Hà Nội');

-- ============================================
-- 3. THÊM DỊCH VỤ (SERVICES)
-- ============================================

INSERT INTO services (ServiceName, Price)
VALUES
    (N'Ăn sáng tại phòng', 250000),
    (N'Massage toàn thân (60 phút)', 1000000),
    (N'Tour du lịch thành phố (4 giờ)', 2000000),
    (N'Dọn phòng bổ sung', 150000),
    (N'Xe đón khách sân bay', 500000),
    (N'Dịch vụ giặt là', 300000),
    (N'Spa facial (50 phút)', 750000),
    (N'Tư vấn du lịch miễn phí', 0),
    (N'Bữa trưa tại nhà hàng', 450000),
    (N'Bữa tối tại nhà hàng', 550000);

-- ============================================
-- 4. THÊM ĐẶTION PHÒNG - NGÀY HÔM NAY
-- ============================================

INSERT INTO reservations (CheckInDate, CheckOutDate, NumberOfGuests, TotalAmonut, Status, CreatedAt, GuestId, UserId)
VALUES
    -- Khách 1: Nguyễn Văn A - Check-in hôm nay
    (
        CAST(CAST(GETDATE() AS DATE) AS DATETIME),
        CAST(CAST(DATEADD(DAY, 1, GETDATE()) AS DATE) AS DATETIME),
        2,
        10000000,
        N'Pending',
        GETDATE(),
        (SELECT TOP 1 GuestId FROM guests WHERE Email = N'nguyenvana@email.com'),
        NULL
    ),

    -- Khách 2: Trần Thị B - Check-in hôm nay
    (
        CAST(CAST(GETDATE() AS DATE) AS DATETIME),
        CAST(CAST(DATEADD(DAY, 2, GETDATE()) AS DATE) AS DATETIME),
        1,
        5000000,
        N'Pending',
        GETDATE(),
        (SELECT TOP 1 GuestId FROM guests WHERE Email = N'tranthib@email.com'),
        NULL
    ),

    -- Khách 3: Lê Minh C - Check-in hôm nay
    (
        CAST(CAST(GETDATE() AS DATE) AS DATETIME),
        CAST(CAST(DATEADD(DAY, 3, GETDATE()) AS DATE) AS DATETIME),
        3,
        15000000,
        N'Pending',
        GETDATE(),
        (SELECT TOP 1 GuestId FROM guests WHERE Email = N'leminhc@email.com'),
        NULL
    );

-- ============================================
-- 5. THÊM ĐẶTION PHÒNG - TRONG TUẦN TỚI
-- ============================================

INSERT INTO reservations (CheckInDate, CheckOutDate, NumberOfGuests, TotalAmonut, Status, CreatedAt, GuestId, UserId)
VALUES
    -- Khách 4: Phạm Quốc D - Check-in 3 ngày sau
    (
        CAST(CAST(DATEADD(DAY, 3, GETDATE()) AS DATE) AS DATETIME),
        CAST(CAST(DATEADD(DAY, 5, GETDATE()) AS DATE) AS DATETIME),
        2,
        8000000,
        N'Pending',
        GETDATE(),
        (SELECT TOP 1 GuestId FROM guests WHERE Email = N'phamquocd@email.com'),
        NULL
    ),

    -- Khách 5: Vũ Hương E - Check-in 5 ngày sau
    (
        CAST(CAST(DATEADD(DAY, 5, GETDATE()) AS DATE) AS DATETIME),
        CAST(CAST(DATEADD(DAY, 7, GETDATE()) AS DATE) AS DATETIME),
        1,
        6000000,
        N'Pending',
        GETDATE(),
        (SELECT TOP 1 GuestId FROM guests WHERE Email = N'vuhuange@email.com'),
        NULL
    );

-- ============================================
-- 6. THÊM ĐẶTION PHÒNG - TRONG THÁNG TỚI
-- ============================================

INSERT INTO reservations (CheckInDate, CheckOutDate, NumberOfGuests, TotalAmonut, Status, CreatedAt, GuestId, UserId)
VALUES
    -- Khách 6: Đặng Minh F - Check-in 10 ngày sau
    (
        CAST(CAST(DATEADD(DAY, 10, GETDATE()) AS DATE) AS DATETIME),
        CAST(CAST(DATEADD(DAY, 12, GETDATE()) AS DATE) AS DATETIME),
        2,
        12000000,
        N'Pending',
        GETDATE(),
        (SELECT TOP 1 GuestId FROM guests WHERE Email = N'dangminhf@email.com'),
        NULL
    );

-- ============================================
-- 7. KẾT QUẢ - HIỂN THỊ DỮ LIỆU ĐÃ THÊM
-- ============================================

PRINT '========== KHÁCH HÀNG ĐÃ THÊM ==========';
SELECT
    GuestId AS 'ID',
    FirstName + ' ' + LastName AS 'Tên khách',
    Email AS 'Email',
    PhoneNumber AS 'Điện thoại',
    Address AS 'Địa chỉ'
FROM guests
WHERE Email LIKE '%@email.com'
ORDER BY GuestId DESC;

PRINT '';
PRINT '========== DỊCH VỤ ĐÃ THÊM ==========';
SELECT
    ServiceId AS 'ID',
    ServiceName AS 'Tên dịch vụ',
    FORMAT(Price, 'N0', 'vi-VN') + ' VNĐ' AS 'Giá'
FROM services
ORDER BY ServiceId DESC;

PRINT '';
PRINT '========== ĐẶT PHÒNG CHỜ CHECK-IN ==========';
SELECT
    r.ReservationId AS 'Mã DP',
    g.FirstName + ' ' + g.LastName AS 'Khách hàng',
    g.PhoneNumber AS 'SĐT',
    CONVERT(VARCHAR(10), r.CheckInDate, 103) AS 'Ngày nhận',
    CONVERT(VARCHAR(10), r.CheckOutDate, 103) AS 'Ngày trả',
    r.NumberOfGuests AS 'Số khách',
    FORMAT(r.TotalAmonut, 'N0', 'vi-VN') + ' VNĐ' AS 'Tổng tiền',
    r.Status AS 'Trạng thái'
FROM reservations r
INNER JOIN guests g ON r.GuestId = g.GuestId
WHERE r.Status = N'Pending'
ORDER BY r.CheckInDate ASC;

PRINT '';
PRINT '========== TÓMLƯỢT ==========';
SELECT
    'Khách hàng' AS 'Item',
    COUNT(*) AS 'Số lượng'
FROM guests
WHERE Email LIKE '%@email.com'
UNION ALL
SELECT
    'Dịch vụ' AS 'Item',
    COUNT(*) AS 'Số lượng'
FROM services
UNION ALL
SELECT
    'Đặt phòng chờ' AS 'Item',
    COUNT(*) AS 'Số lượng'
FROM reservations
WHERE Status = N'Pending';

PRINT '';
PRINT '✓ Dữ liệu test đã được chèn thành công!';
PRINT '✓ Hãy truy cập: http://localhost:8080/receptionist/check-in';
PRINT '';

