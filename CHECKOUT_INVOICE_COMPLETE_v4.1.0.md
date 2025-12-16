# ✅ HOÀN THÀNH - CHỨC NĂNG CHECK-OUT + IN HÓA ĐƠN (v4.1.0)

## 🎯 YÊU CẦU HOÀN THÀNH

✅ **Danh sách phòng cần check-out** - Hiển thị khách đã check-in  
✅ **Confirm dialog** - Xác nhận trước khi trả phòng  
✅ **In hóa đơn** - In invoice sau check-out  
✅ **Tìm kiếm & Phân trang** - Theo tên khách, 10 bản ghi/trang  

---

## ✅ ĐÃ THỰC HIỆN

### 1️⃣ **Controller - ReceptionistController.java**

**Endpoint 1: GET /receptionist/check-out**
```java
@GetMapping("/check-out")
public String showCheckOutPage(
    @RequestParam(value = "search", required = false) String search,
    @RequestParam(value = "page", defaultValue = "0") int page,
    @RequestParam(value = "size", defaultValue = "10") int size,
    Model model)
```
- Lấy danh sách Reservations (status = "Confirmed")
- Tìm kiếm theo tên khách
- Phân trang (10 bản ghi/trang)
- Trả về template `receptionist/check-out`

**Endpoint 2: POST /receptionist/check-out**
```java
@PostMapping("/check-out")
public String processCheckOut(
    @RequestParam("reservationId") Integer reservationId,
    RedirectAttributes redirectAttributes)
```
- Gọi `checkOutReservation(reservationId)`
- Cập nhật Reservation.status = "Checked Out"
- Cập nhật Room.roomStatus = "AVAILABLE"
- Redirect → `GET /receptionist/invoice/{reservationId}`

**Endpoint 3: GET /receptionist/invoice/{id}**
```java
@GetMapping("/invoice/{reservationId}")
public String showInvoice(
    @PathVariable("reservationId") Integer reservationId,
    Model model)
```
- Hiển thị hóa đơn check-out
- Trả về template `receptionist/invoice`

### 2️⃣ **Repository - ReservationRepository.java**

**Method mới:**
```java
@Query("SELECT r FROM Reservation r WHERE r.status = :status 
        AND (LOWER(r.guest.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) 
        OR LOWER(r.guest.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
Page<Reservation> findByStatusAndGuestFullNameContainingIgnoreCase(
    @Param("status") String status,
    @Param("searchTerm") String searchTerm,
    Pageable pageable);
```

### 3️⃣ **Template - check-out.html** (NEW)

**Cấu trúc:**
```html
✅ Header: "🚪 Danh Sách Check-out"
✅ Search Form: [Tìm kiếm] [Reset]
✅ Table: STT | Mã ĐP | Khách | Phòng | Check-in | Check-out | Tổng $ | Hành động
✅ Buttons: [🚪 Trả Phòng] (red)
✅ Pagination: [1] [2] [3]...
✅ Empty State: Không có khách nào cần check-out
✅ Modal: Xác nhận trả phòng
```

**JavaScript:**
- `openCheckOutModal(reservationId, guestName)` - Mở modal
- `closeCheckOutModal()` - Đóng modal
- `confirmCheckOut()` - Submit POST request
- Event listeners cho buttons `.checkout-btn`

**Data Attributes:**
```html
<button class="checkout-btn"
        th:data-reservation-id="${reservation.reservationId}"
        th:data-guest-name="${reservation.guest.fullName}">
```

### 4️⃣ **Template - invoice.html** (NEW)

**Cấu trúc:**
```html
✅ Header: 🏨 KHÁCH SẠN Logo
✅ Invoice #: DP{reservationId}
✅ Guest Info: Tên, Email, Phone (phoneNumber), Quốc tịch
✅ Room Info: Phòng, Hạng, Check-in/out ngày giờ
✅ Table 1: Phòng (Loại | Phòng | Số đêm | Giá/đêm | Thành tiền)
✅ Table 2: Dịch vụ (Tên | SL | Giá | Thành tiền)
✅ Summary: Giá phòng + Giá DV + Tổng cộng
✅ Buttons: [🖨️ In] [← Quay Lại]
✅ Print-friendly CSS (@media print)
```

**Fixes Applied:**
- ✅ Fixed: `guest.phone` → `guest.phoneNumber`
- ✅ Định dạng tiền tệ: 1.000.000₫

---

## 📊 FLOW HOÀN CHỈNH

```
1. Sidebar: Click "Check-out"
   ↓
2. GET /receptionist/check-out
   ↓
3. ReceptionistController.showCheckOutPage()
   ├─ Lấy Reservations (status="Confirmed")
   ├─ Tìm kiếm theo tên (nếu có)
   └─ Phân trang (page, size=10)
   ↓
4. Render check-out.html
   ├─ Danh sách table
   ├─ Search form
   └─ [🚪 Trả Phòng] buttons
   ↓
5. User click "🚪 Trả Phòng"
   ↓
6. JavaScript: openCheckOutModal(reservationId, guestName)
   ├─ Đọc data attributes
   ├─ Fill modal content
   └─ Show modal
   ↓
7. Modal confirm dialog
   ├─ [✕ Hủy] → closeCheckOutModal()
   └─ [✓ Xác Nhận] → confirmCheckOut()
   ↓
8. confirmCheckOut() → POST /receptionist/check-out
   ├─ reservationId
   └─ Form submit
   ↓
9. ReceptionistController.processCheckOut()
   ├─ receptionistService.checkOutReservation(id)
   ├─ Update: Reservation.status = "Checked Out"
   ├─ Update: Room.roomStatus = "AVAILABLE"
   └─ Redirect → /receptionist/invoice/{id}
   ↓
10. GET /receptionist/invoice/{reservationId}
    ↓
11. ReceptionistController.showInvoice()
    ├─ Lấy Reservation details
    └─ Render invoice.html
    ↓
12. invoice.html render hóa đơn
    ├─ Khách hàng: tên, email, phone, nationality
    ├─ Phòng: tên, loại, check-in/out
    ├─ Chi tiết phòng: ngày, giá, thành tiền
    ├─ Chi tiết dịch vụ: tên, SL, giá, thành tiền
    ├─ Tổng cộng
    └─ Buttons: [🖨️ In] [← Quay Lại]
    ↓
13. User click "🖨️ In"
    ↓
14. window.print() → In hóa đơn
```

---

## 🎨 GIAO DIỆN

### Check-out Page
```
╔════════════════════════════════════════════════════╗
║ 🚪 Danh Sách Check-out                             ║
╠════════════════════════════════════════════════════╣
║                                                    ║
║ 🔍 [Tìm kiếm tên khách...] [Tìm] [Reset]         ║
║                                                    ║
║ ╔═══════════════════════════════════════════════╗ ║
║ ║STT│Mã ĐP│Khách    │Phòng │C-in │C-out │$  │ ║
║ ╟───┼─────┼─────────┼──────┼─────┼──────┼────╢ ║
║ ║1  │DP1  │John Doe │JS130 │14:00│16:00│2.5M│ ║
║ ║   │     │john@... │      │     │     │    │ ║
║ ║───┼─────┼──��──────┼──────┼─────┼──────┼────┤ ║
║ ║2  │DP2  │Jane Sm. │SUP5  │14:00│17:00│3.0M│ ║
║ ║   │     │jane@... │      │     │     │    │ ║
║ ╚═══════════════════════════════════════════════╝ ║
║ [1] [2] [3] ... (Pagination)                     ║
║                                                    ║
╚════════════════════════════════════════════════════╝

Modal: Confirm Checkout
┌──────────────────────────────────┐
│ 🚪 Xác Nhận Trả Phòng            │
├──────────────────────────────────┤
│                                  │
│ Khách hàng: John Doe             │
│ Mã ĐP: DP1                       │
│ ⚠️ Bạn chắc chắn muốn trả phòng? │
│                                  │
│ [✕ Hủy] [✓ Xác Nhận]            │
│                                  │
└──────────────────────────────────┘
```

### Invoice Page
```
┌─────────────────────────────────────────────┐
│ 🏨 KHÁCH SẠN                                 │
│ Hotel Management System                      │
│              HÓA ĐƠN CHECK-OUT              │
│              DP12345                        │
│              16/12/2025 14:30               │
├─────────────────────────────────────────────┤
│                                             │
│ 👤 KHÁCH HÀNG        🛏️ PHÒNG             │
│ Tên: John Doe        Phòng: JS1301         │
│ Email: john@...      Hạng: Suite           │
│ Phone: +84 123...    Nhận: 14/12 14:00     │
│ QT: USA              Trả: 16/12 17:00      │
│                                             │
├─────────────────────────────────────────────┤
│ 📋 CHI TIẾT PHÒNG                           │
│ ┌────────────────────────────────────────┐ │
│ │Loại │Phòng│Đêm│Giá/Đêm │Thành Tiền   │ │
│ ├────────────────────────────────────────┤ │
│ │Suite│JS13│2  │1.5M    │3.0M          │ │
│ └────────────────────────────────────────┘ │
│                                             │
│ 📋 CHI TIẾT DỊCH VỤ                        │
│ ┌────────────────────────────────────────┐ │
│ │DV   │SL │Giá   │Thành Tiền          │ │
│ ├────────────────────────────────────────┤ │
│ │WiFi │1  │500K  │500K                │ │
│ │Spa  │2  │300K  │600K                │ │
│ └────────────────────────────────────────┘ │
│                                             │
│ Giá Phòng: 3.0M                            │
│ Giá DV: 1.1M                               │
│ ────────────────                           │
│ TỔNG CỘNG: 4.1M                            │
│                                             │
│ [🖨️ In] [← Quay Lại]                      │
│                                             │
└─────────────────────────────────────────────┘
```

---

## 🚀 KHỞI ĐỘNG

```bash
# Terminal 1: Chạy ứng dụng
cd "D:\Semester 5\HSF302_Block5\HotelManagementProject\HotelManagement"
mvnw.cmd spring-boot:run

# Terminal 2: Test
→ http://localhost:8080/receptionist/check-out
```

### Test Steps:
1. ✅ Xem danh sách khách đã check-in (status="Confirmed")
2. ✅ Tìm kiếm theo tên khách
3. ✅ Phân trang (click page 1, 2, 3...)
4. ✅ Click "🚪 Trả Phòng"
5. ✅ Thấy modal confirm
6. ✅ Click "✓ Xác Nhận"
7. ✅ Thấy invoice
8. ✅ Click "🖨️ In Hóa Đơn"
9. ✅ In được hóa đơn

---

## 📝 FILES MODIFIED/CREATED

### Modified (3 files)
- ✅ `ReceptionistController.java` - Thêm 3 endpoints
- ✅ `ReservationRepository.java` - Thêm search method
- ✅ `invoice.html` - Fix `phoneNumber` property

### Created (1 file)
- ✅ `check-out.html` - Template check-out list + modal

---

## ✅ COMPILE & BUILD STATUS

```
✅ Compile: SUCCESS (0 errors)
✅ Build: READY
✅ Server: RUNNING
✅ Templates: FIXED
```

---

## 🔍 FIXES APPLIED

### Fix 1: Template Syntax
```
❌ Before: th:replace="~{${view} :: content}"
✅ After: return "receptionist/check-out" (direct template)
```

### Fix 2: Onclick Event
```
❌ Before: th:onclick="'openCheckOutModal(' + ${id} + ...'"
✅ After: th:data-reservation-id="${id}" + JavaScript listener
```

### Fix 3: Guest Property
```
❌ Before: ${reservation.guest.phone}
✅ After: ${reservation.guest.phoneNumber}
```

---

## 💡 KEY FEATURES

| Feature | Status | Details |
|---------|--------|---------|
| List Check-out | ✅ | Danh sách reservations (Confirmed) |
| Search | ✅ | Tìm kiếm theo tên khách |
| Pagination | ✅ | 10 bản ghi/trang |
| Modal Confirm | ✅ | Xác nhận trước trả phòng |
| Check-out | ✅ | Update status + room |
| Invoice | ✅ | Chi tiết hóa đơn |
| Print | ✅ | Print-friendly CSS |
| Responsive | ✅ | Mobile-friendly |

---

## 📊 DATABASE UPDATES

### Reservation Table
```sql
-- Before
status = "Confirmed"

-- After (check-out)
status = "Checked Out"
```

### Room Table
```sql
-- Before
roomStatus = "OCCUPIED"

-- After (check-out)
roomStatus = "AVAILABLE"
```

---

## ✅ PROJECT STATUS

```
Phase 1: Check-in  ✅ DONE
Phase 2: Add Service ✅ DONE
Phase 3: Check-out ✅ DONE
Phase 4: Invoice ✅ DONE

Total Features: 4/4 COMPLETE ✅
```

---

**Version:** 4.1.0  
**Date:** December 16, 2025  
**Compiler:** Maven 3.9.x  
**Java:** 17+  
**Spring Boot:** 3.x  

### Chạy ngay:
```bash
mvnw.cmd spring-boot:run
→ http://localhost:8080/receptionist/check-out
```

🚀 **READY TO DEPLOY!** 🚀

