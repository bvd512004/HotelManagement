# Hướng Dẫn Tính Toán Giá Tiền - Hotel Management System

## 📋 Tóm Tắt Thay Đổi

**Ngày cập nhật:** 17/12/2025

Logic tính tiền đã được tích hợp hoàn chỉnh để **tính toán chính xác tổng tiền = (Giá phòng × Số đêm) + Tiền dịch vụ**.

---

## 🔄 Cách Hoạt Động

### Công Thức Tính Tiền

```
Tổng Tiền = (BasePrice × Số Đêm) + Tổng Tiền Dịch Vụ
```

### Ví Dụ Minh Họa

**Scenario 1: Chỉ có tiền phòng**
```
- Phòng: Deluxe
- BasePrice: 2,000,000₫/đêm
- Check-in: 17/12/2025
- Check-out: 20/12/2025
- Số đêm: 3

Tính toán:
- Tiền phòng = 2,000,000 × 3 = 6,000,000₫
- Tiền dịch vụ = 0₫
- Tổng cộng = 6,000,000₫
```

**Scenario 2: Có tiền phòng + dịch vụ**
```
- Phòng: Deluxe (2,000,000₫/đêm, 3 đêm) = 6,000,000₫
- Dịch vụ 1: Đặc sản (Spa) = 500,000₫ × 2 = 1,000,000₫
- Dịch vụ 2: Giặt ủi = 200,000₫ × 1 = 200,000₫

Tính toán:
- Tiền phòng = 2,000,000 × 3 = 6,000,000₫
- Tiền dịch vụ = 1,000,000 + 200,000 = 1,200,000₫
- Tổng cộng = 7,200,000₫
```

---

## 📝 Chi Tiết Thay Đổi

### 1. **ReceptionistService.java**

#### ✨ Thêm 2 Helper Methods

**a) `calculateTotalAmount(Reservation reservation)`**

```java
/**
 * Tính tổng tiền = (Giá phòng × Số đêm) + Tổng dịch vụ
 * @param reservation đối tượng Reservation
 * @return tổng tiền cần thanh toán
 */
private double calculateTotalAmount(Reservation reservation) {
    // Tính tiền phòng = BasePrice × Số đêm
    double roomAmount = 0;
    if (reservation.getReservation_rooms() != null && !reservation.getReservation_rooms().isEmpty()) {
        long daysOfStay = getDaysOfStay(reservation.getCheckInDate(), reservation.getCheckOutDate());
        
        for (Reservation_Room resRoom : reservation.getReservation_rooms()) {
            com.hsf302.hotelmanagement.entity.RoomType roomType = resRoom.getRoom().getRoomType();
            if (roomType != null) {
                roomAmount += roomType.getBasePrice() * daysOfStay;
            }
        }
    }

    // Tính tiền dịch vụ
    double serviceAmount = 0;
    if (reservation.getReservation_services() != null && !reservation.getReservation_services().isEmpty()) {
        for (Reservation_Service resService : reservation.getReservation_services()) {
            serviceAmount += resService.getPriceAtTheTime();
        }
    }

    return roomAmount + serviceAmount;
}
```

**Chức năng:**
- Tính tiền phòng = BasePrice × Số đêm
- Tính tiền dịch vụ bằng cách cộng `PriceAtTheTime` của tất cả dịch vụ
- Trả về tổng = Tiền phòng + Tiền dịch vụ

---

**b) `getDaysOfStay(Date checkInDate, Date checkOutDate)`**

```java
/**
 * Tính số đêm lưu trú
 * @param checkInDate ngày nhận phòng
 * @param checkOutDate ngày trả phòng
 * @return số đêm lưu trú (tối thiểu 1 đêm)
 */
private long getDaysOfStay(Date checkInDate, Date checkOutDate) {
    if (checkInDate == null || checkOutDate == null) {
        return 1; // Mặc định 1 đêm nếu thiếu dữ liệu
    }
    
    long diffInMillies = checkOutDate.getTime() - checkInDate.getTime();
    long days = diffInMillies / (1000 * 60 * 60 * 24);
    
    // Đảm bảo tối thiểu 1 đêm
    return Math.max(1, days);
}
```

**Chức năng:**
- Tính khoảng thời gian giữa check-in và check-out
- Chuyển đổi thành số ngày
- Đảm bảo tối thiểu 1 đêm (nếu check-in và check-out cùng ngày)

---

#### 🔄 Cập Nhật `addServiceToReservation()` Method

**Thay đổi:**
```java
// CỰ (OLD) - Chỉ cộng thêm tiền dịch vụ
res.setTotalAmount(res.getTotalAmount() + additionalAmount);

// MỚI - Tính lại toàn bộ từ đầu
double totalAmount = calculateTotalAmount(res);
res.setTotalAmount(totalAmount);
```

**Lợi ích:**
- ✅ Tránh sai sót do cộng thêm
- ✅ Tính toán lại toàn bộ tiền phòng + dịch vụ
- ✅ Nếu dữ liệu bị thay đổi, hệ thống tự động điều chỉnh

---

#### 🔄 Cập Nhật `addMultipleServices()` Method

**Thay đổi:**
```java
// CỨ (OLD) - Chỉ cộng thêm tiền dịch vụ
res.setTotalAmount(res.getTotalAmount() + additionalAmount);

// MỚI - Tính lại toàn bộ từ đầu
double totalAmount = calculateTotalAmount(res);
res.setTotalAmount(totalAmount);
```

---

### 2. **check-in.html**

#### 📊 Cập Nhật Bảng Hiển Thị Giá

**Trước:**
```html
<!-- Giá tiền/ngày = TotalAmount / Số ngày -->
<td class="price" th:text="${#numbers.formatInteger(pricePerDay, 0, 'COMMA') + '₫'}"></td>

<!-- Tổng cộng = TotalAmount -->
<td class="price" th:text="${#numbers.formatInteger(reservation.totalAmount, 0, 'COMMA') + '₫'}"></td>
```

**Sau:**
```html
<!-- Giá tiền/ngày = BasePrice của phòng -->
<td class="price" th:text="${#numbers.formatInteger(basePrice, 0, 'COMMA') + '₫'}"></td>

<!-- Tổng cộng = BasePrice × Số ngày -->
<td class="price" th:text="${#numbers.formatInteger(totalPrice, 0, 'COMMA') + '₫'}"></td>
```

**Lợi ích:**
- ✅ Hiển thị giá phòng chính xác (BasePrice)
- ✅ Tính tổng chính xác từ giá × ngày
- ✅ User dễ hiểu logic tính toán

---

## 🎯 Flow Tính Tiền

```
┌─────────────────────────────────────────────────────────┐
│ 1. Khách đặt phòng (Booking)                            │
│    - CheckInDate, CheckOutDate                          │
│    - Số phòng, loại phòng                               │
│    - TotalAmount = 0                                    │
└──────────────────────┬──────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────┐
│ 2. Tiếp tân add dịch vụ (addServiceToReservation)      │
│    - Service: Spa (500,000₫)                            │
│    - Quantity: 2                                        │
│    - PriceAtTheTime: 1,000,000₫                         │
└──────────────────────┬──────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────┐
│ 3. Hệ thống tính lại TotalAmount (calculateTotalAmount) │
│    - Tiền phòng = 2,000,000₫ × 3 đêm = 6,000,000₫     │
│    - Tiền dịch vụ = 1,000,000₫                          │
│    - TotalAmount = 6,000,000 + 1,000,000 = 7,000,000₫  │
└──────────────────────┬──────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────┐
│ 4. Hiển thị trên giao diện (check-in.html)             │
│    - Giá tiền/ngày: 2,000,000₫                          │
│    - Lưu trú: 3 ngày                                    │
│    - Tổng cộng: 7,000,000₫                              │
└─────────────────────────────────────────────────────────┘
```

---

## 🔍 Testing Scenarios

### Test Case 1: Tính Tiền Phòng Đơn Giản
```
Input:
- Reservation ID: 1
- Room Type: Standard
- BasePrice: 1,500,000₫
- CheckInDate: 17/12/2025
- CheckOutDate: 20/12/2025

Expected Output:
- Days: 3
- Room Amount: 1,500,000 × 3 = 4,500,000₫
- Service Amount: 0₫
- Total Amount: 4,500,000₫
```

### Test Case 2: Tính Tiền Với Dịch Vụ
```
Input:
- Reservation ID: 2
- Room Type: Deluxe
- BasePrice: 2,500,000₫
- CheckInDate: 17/12/2025
- CheckOutDate: 19/12/2025
- Services: 
  * Spa (500,000₫) × 1 = 500,000₫
  * Room Service (200,000₫) × 2 = 400,000₫

Expected Output:
- Days: 2
- Room Amount: 2,500,000 × 2 = 5,000,000₫
- Service Amount: 500,000 + 400,000 = 900,000₫
- Total Amount: 5,900,000₫
```

### Test Case 3: Add Dịch Vụ Vào Đặt Phòng Hiện Tại
```
Input:
- Existing TotalAmount: 4,500,000₫
- New Service: Laundry (150,000₫) × 3 = 450,000₫

Process:
1. addServiceToReservation() được gọi
2. Dịch vụ được thêm vào Reservation_Service
3. calculateTotalAmount() tính lại:
   - Tiền phòng: 1,500,000 × 3 = 4,500,000₫
   - Tiền dịch vụ mới: 450,000₫
   - TotalAmount = 4,950,000₫

Output:
- TotalAmount: 4,950,000₫ (không phải 4,500,000 + 450,000)
```

---

## ⚠️ Lưu Ý Quan Trọng

1. **Đơn vị tiền tệ**: Tất cả giá tiền sử dụng **Đồng Việt (VNĐ - ₫)**

2. **Số đêm tối thiểu**: Nếu check-in và check-out cùng ngày, hệ thống vẫn tính **tối thiểu 1 đêm**

3. **Thay đổi dịch vụ**: Khi add, remove, hoặc update dịch vụ, hệ thống **tính lại toàn bộ từ đầu** (không tích lũy)

4. **BasePrice**: Luôn lấy từ `RoomType.basePrice` tại thời điểm tính toán

5. **Cập nhật hóa đơn**: Khi thanh toán, hệ thống sử dụng `TotalAmount` cuối cùng sau tất cả điều chỉnh

---

## 📱 API Endpoints Liên Quan

### 1. Thêm Dịch Vụ
```
POST /receptionist/add-service
Parameters:
  - reservationId: Integer
  - serviceId: Integer
  - quantity: Integer

Response:
  - Updated Reservation with recalculated TotalAmount
```

### 2. Xem Danh Sách Check-in
```
GET /receptionist/check-in
Parameters:
  - fromDate: String (yyyy-MM-dd)
  - toDate: String (yyyy-MM-dd)
  - search: String (optional)

Response:
  - List of Reservations with correct pricing
```

---

## 🐛 Troubleshooting

### Vấn đề: Tổng tiền không khớp
**Giải pháp:**
1. Kiểm tra `checkInDate` và `checkOutDate` có null không
2. Xác nhận `RoomType.basePrice` có giá trị không
3. Đảm bảo `Reservation_Service.priceAtTheTime` được set đúng

### Vấn đề: Số đêm hiển thị sai
**Giải pháp:**
1. Xem lại `getDaysOfStay()` method
2. Kiểm tra time zone khi so sánh Date

---

## 📞 Hỗ Trợ

Nếu có câu hỏi, vui lòng liên hệ team dev hoặc tham khảo:
- `ReceptionistService.java` - Logic tính tiền
- `check-in.html` - Hiển thị giao diện
- Database schema - `reservations`, `reservation_rooms`, `reservation_services`

