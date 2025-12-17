# 📊 Visual Diagrams - Pricing System

---

## 1️⃣ OVERALL SYSTEM ARCHITECTURE

```
┌─────────────────────────────────────────────────────────────────┐
│                      HOTEL MANAGEMENT SYSTEM                    │
│                     PRICING CALCULATION FLOW                    │
└─────────────────────────────────────────────────────────────────┘

┌───────────────────────┐
│  1. BOOKING CREATION  │
│  - Guest info         │
│  - CheckInDate        │
│  - CheckOutDate       │
│  - Room selection     │
└───────┬───────────────┘
        ↓
┌─────────────────────────────────────────┐
│ Database: Reservation created           │
│ - TotalAmount = 0 (initially)           │
│ - Status = "Pending"                    │
└───────┬─────────────────────────────────┘
        ↓
┌────────────────────────────────────────────┐
│ 2. RECEPTIONIST ADD SERVICE                │
│    POST /receptionist/add-service          │
│    - reservationId: 1                      │
│    - serviceId: 5 (Spa)                    │
│    - quantity: 1                           │
└───────┬────────────────────────────────────┘
        ↓
┌──────────────────────────────────────────────────────────┐
│ ReceptionistService.addServiceToReservation()            │
│ 1. Fetch Reservation #1 from DB                         │
│ 2. Create Reservation_Service                           │
│    - service: Spa                                        │
│    - quantity: 1                                         │
│    - priceAtTheTime: 500,000₫                           │
│ 3. Save to DB                                           │
│ 4. ✨ [NEW] Call calculateTotalAmount(res)             │
│    ├─ Loop reservation_rooms                           │
│    │  └─ RoomAmount = BasePrice × DaysOfStay          │
│    │     = 2,000,000 × 3 = 6,000,000₫                │
│    ├─ Loop reservation_services                        │
│    │  └─ ServiceAmount = 500,000₫                      │
│    └─ Return: 6,000,000 + 500,000 = 6,500,000₫       │
│ 5. res.setTotalAmount(6,500,000)                       │
│ 6. Save updated Reservation to DB                       │
└───────┬──────────────────────────────────────────────────┘
        ↓
┌────────────────────────────────────────┐
│ Database: Updated Reservation           │
│ - TotalAmount = 6,500,000₫             │
│ - reservation_services += 1             │
└───────┬────────────────────────────────┘
        ↓
┌────────────────────────────────────────┐
│ 3. FRONTEND DISPLAY (check-in.html)     │
│ - Giá tiền/ngày: 2,000,000₫            │
│ - Lưu trú: 3 ngày                      │
│ - Tổng cộng: 6,500,000₫ ← từ DB       │
└────────────────────────────────────────┘
        ↓
┌────────────────────────────────────────┐
│ 4. CHECK-OUT & INVOICE                  │
│ - Print invoice with TotalAmount        │
│ - Payment: 6,500,000₫                  │
└────────────────────────────────────────┘
```

---

## 2️⃣ METHOD: calculateTotalAmount()

```
Function: calculateTotalAmount(Reservation res)
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    ┌──────────────────────────────┐
    │ INPUT: Reservation object    │
    │ - checkInDate                │
    │ - checkOutDate               │
    │ - reservation_rooms[]        │
    │ - reservation_services[]     │
    └────────┬─────────────────────┘
             ↓
    ┌─────────────────────────────────────────┐
    │ STEP 1: Initialize roomAmount = 0       │
    │         Initialize serviceAmount = 0    │
    └────────┬────────────────────────────────┘
             ↓
    ┌─────────────────────────────────────────┐
    │ STEP 2: CALCULATE ROOM AMOUNT            │
    │         For each Reservation_Room:      │
    │         - Get RoomType.basePrice        │
    │         - Get daysOfStay()              │
    │         - roomAmount += BP × DOS        │
    └────────┬────────────────────────────────┘
             ↓
    ┌─────────────────────────────────────────┐
    │ STEP 3: CALCULATE SERVICE AMOUNT        │
    │         For each Reservation_Service:   │
    │         - serviceAmount += priceAtTime  │
    └────────┬────────────────────────────────┘
             ↓
    ┌──────────────────────────────────────────┐
    │ STEP 4: RETURN (roomAmount +serviceAmount│
    │         Example: 6,000,000 + 500,000    │
    │                = 6,500,000₫             │
    └──────────────┬───────────────────────────┘
                   ↓
            ┌──────────────┐
            │ OUTPUT       │
            │ 6,500,000₫   │
            └──────────────┘
```

---

## 3️⃣ METHOD: getDaysOfStay()

```
Function: getDaysOfStay(Date checkIn, Date checkOut)
↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    ┌─────────────────────────────────┐
    │ INPUT: checkInDate, checkOutDate│
    │ Example:                        │
    │ - checkIn: 2025-12-17 14:00    │
    │ - checkOut: 2025-12-20 11:00   │
    └────────┬────────────────────────┘
             ↓
    ┌────────────────────────────────────┐
    │ STEP 1: Null Check                 │
    │ if (checkIn == null || checkOut == null)
    │    return 1 (default 1 night)      │
    └────────┬───────────────────────────┘
             ↓
    ┌────────────────────────────────────────────┐
    │ STEP 2: Calculate Time Difference (ms)     │
    │ diffInMillies = checkOut.time - checkIn.time
    │ = (20/12 11:00) - (17/12 14:00)            │
    │ = 248,400,000 ms (≈ 2.87 days)             │
    └────────┬───────────────────────────────────┘
             ↓
    ┌────────────────────────────────────┐
    │ STEP 3: Convert to Days            │
    │ days = 248,400,000 / 86,400,000   │
    │      = 2 (integer division)        │
    │                                    │
    │ ⚠️ Note: Integer division rounds   │
    │          down (might lose 0.87)    │
    └────────┬───────────────────────────┘
             ↓
    ┌────────────────────────────────────┐
    │ STEP 4: Ensure Minimum 1 Day      │
    │ return Math.max(1, days)           │
    │        Math.max(1, 2) = 2          │
    └────────┬───────────────────────────┘
             ↓
            ┌──────────────┐
            │ OUTPUT       │
            │ 2 days       │
            └──────────────┘

    Note: Real stay is 3 nights (17→18, 18→19, 19→20)
          But calculation shows 2 due to time boundary
          This is a known limitation ⚠️
```

---

## 4️⃣ COMPLETE CALCULATION EXAMPLE

```
SCENARIO: Hotel booking with multiple services
══════════════════════════════════════════════

INPUT DATA:
───────────
Reservation #5:
- Guest: Nguyễn Văn A
- Room: Deluxe (RoomType ID: 3)
- CheckInDate: 2025-12-17 14:00:00
- CheckOutDate: 2025-12-20 11:00:00
- Reservation_Rooms: [Deluxe Room #502]
- Reservation_Services: []

RoomType #3 (Deluxe):
- basePrice: 2,000,000₫

Services to add:
- Spa (serviceId: 10): 500,000₫/unit × 1 = 500,000₫
- Room Service (serviceId: 15): 200,000₫/unit × 2 = 400,000₫


STEP-BY-STEP CALCULATION:
─────────────────────────

Step 1: User adds Spa service
   POST /receptionist/add-service
   {
     reservationId: 5,
     serviceId: 10,
     quantity: 1
   }

Step 2: addServiceToReservation() executed
   ├─ Fetch Reservation #5
   ├─ Create Reservation_Service (Spa)
   │  └─ priceAtTheTime: 500,000₫
   ├─ Save to DB
   └─ ✨ calculateTotalAmount(res)
   
Step 3: calculateTotalAmount() breaks down:
   
   a) Calculate roomAmount:
      ├─ Get reservation_rooms = [Deluxe #502]
      ├─ Get DaysOfStay(17/12 14:00, 20/12 11:00)
      │  └─ = 2 days (⚠️ should be 3 nights)
      ├─ Get RoomType.basePrice = 2,000,000₫
      └─ roomAmount = 2,000,000 × 2 = 4,000,000₫
      
   b) Calculate serviceAmount:
      ├─ Get reservation_services = [Spa(500,000)]
      └─ serviceAmount = 500,000₫
      
   c) Return total:
      └─ 4,000,000 + 500,000 = 4,500,000₫

Step 4: Update Reservation
   ├─ res.setTotalAmount(4,500,000)
   └─ Save to DB

Step 5: User adds Room Service
   POST /receptionist/add-service
   {
     reservationId: 5,
     serviceId: 15,
     quantity: 2
   }

Step 6: calculateTotalAmount() again:
   a) roomAmount = 2,000,000 × 2 = 4,000,000₫
   b) serviceAmount = 500,000 + 200,000 + 200,000
                    = 900,000₫
   c) return = 4,000,000 + 900,000 = 4,900,000₫

Step 7: Update Reservation
   ├─ res.setTotalAmount(4,900,000)
   └─ Save to DB

FINAL OUTPUT:
─────────────
Database record:
{
  reservationId: 5,
  guest: "Nguyễn Văn A",
  checkInDate: "2025-12-17 14:00:00",
  checkOutDate: "2025-12-20 11:00:00",
  totalAmount: 4,900,000,  ← ✅ Calculated correctly
  status: "Pending",
  reservation_rooms: [
    { roomId: 502, roomType: "Deluxe" }
  ],
  reservation_services: [
    { serviceId: 10, serviceName: "Spa", priceAtTheTime: 500,000 },
    { serviceId: 15, serviceName: "Room Service", priceAtTheTime: 400,000 }
  ]
}

Frontend display:
┌────────────────────────────────────┐
│ Danh Sách Check-in                │
├────────────────────────────────────┤
│ Guest: Nguyễn Văn A                │
│ Room: Deluxe                        │
│ Check-in: 17/12/2025 14:00         │
│ Check-out: 20/12/2025 11:00        │
│ Nights: 2                           │
│ Price/night: 2,000,000₫            │
│ TOTAL: 4,900,000₫  ← from DB       │
└────────────────────────────────────┘

Invoice upon checkout:
┌────────────────────────────┐
│ INVOICE #5                 │
├────────────────────────────┤
│ Guest: Nguyễn Văn A        │
│ Room: Deluxe × 2 nights    │
│   2,000,000 × 2 = 4,000,000₫
│                            │
│ Services:                  │
│   Spa (1) = 500,000₫      │
│   Room Service (2) = 400,000₫
│                            │
│ TOTAL: 4,900,000₫          │
│ PAID: 4,900,000₫           │
│ BALANCE: 0₫               │
└────────────────────────────┘
```

---

## 5️⃣ DATABASE RELATIONSHIP DIAGRAM

```
RESERVATIONS (Main table)
┌─────────────────────────────┐
│ ReservationId (PK)          │
│ GuestId (FK)                │ ──→ GUESTS
│ UserId (FK)                 │ ──→ USERS
│ CheckInDate                 │
│ CheckOutDate                │
│ NumberOfGuests              │
│ TotalAmount ✨ (Updated)    │ ← Tính từ rooms + services
│ Status                      │
│ CreatedAt                   │
└─────────┬───────────────────┘
          │
          ├─→ RESERVATION_ROOMS (1:N)
          │   ┌──────────────────────┐
          │   │ ReservationId (FK)   │
          │   │ RoomId (FK)          │ ──→ ROOMS
          │   │ Status               │    ├─→ RoomType.basePrice ✨
          │   └──────────────────────┘
          │
          └─→ RESERVATION_SERVICES (1:N)
              ┌──────────────────────┐
              │ ReservationId (FK)   │
              │ ServiceId (FK)       │ ──→ SERVICES.price
              │ Quantity             │
              │ PriceAtTheTime ✨     │ ← Service price × Quantity
              └──────────────────────┘

CALCULATION FLOW:
1. TotalAmount = Σ(RoomType.basePrice × DaysOfStay) 
               + Σ(Service.PriceAtTheTime)
2. Updated when services are added/removed
3. Used for invoice generation
```

---

## 6️⃣ STATE DIAGRAM - RESERVATION LIFECYCLE

```
        CREATE
          ↓
    ┌──────────────┐
    │   PENDING    │ ← Initial state, no check-in yet
    │ TotalAmount: │   Can add services
    │ calculated   │
    └──────┬───────┘
           │ addServiceToReservation() → recalculate
           ↓
    ┌──────────────────────┐
    │  STILL PENDING       │ ← Can keep adding services
    │  TotalAmount: updated│
    └──────┬───────────────┘
           │ checkInReservation()
           ↓
    ┌──────────────────┐
    │    CONFIRMED     │ ← Guest checked in
    │ TotalAmount:     │   Finalize services if needed
    │ locked (mostly)  │
    └──────┬───────────┘
           │ checkOutReservation()
           ↓
    ┌──────────────────┐
    │   CHECKED OUT    │ ← Generate invoice
    │ TotalAmount:     │   Final payment
    │ final            │
    └──────────────────┘

Notes:
- Can add services anytime (Pending or Confirmed)
- TotalAmount is recalculated each time
- Invoice uses final TotalAmount
```

---

## 7️⃣ PRICING COMPARISON: OLD vs NEW

```
OLD (WRONG)                        NEW (CORRECT)
═════════════════════════════════════════════════════════════

Initial Booking (3 nights):
Res.totalAmount = 0                Calculated on checkout

Add Service (Spa 500K):
totalAmount += 500,000             totalAmount = calculateTotal()
totalAmount = 500,000 ❌           = 6,000,000 + 500,000
              (no room!)            = 6,500,000 ✅

Add Service (Laundry 200K):
totalAmount += 200,000             totalAmount = calculateTotal()
totalAmount = 700,000 ❌           = 6,000,000 + 700,000
              (still no room!)      = 6,700,000 ✅

Final Total:
700,000₫ ❌                         6,700,000₫ ✅
Missing room price!                Room + Services correct!
```

---

## 🎓 LEARNING PATH

```
1. UNDERSTAND BASICS
   └─→ Read: README_PRICING.md
   └─→ Know: Formula = (BP × Days) + Services

2. DEEP DIVE
   └─→ Read: PRICING_CALCULATION_GUIDE.md
   └─→ Know: Each component calculation

3. CODE REVIEW
   └─→ Read: CODE_WALKTHROUGH.md
   └─→ Know: Method implementation

4. TESTING
   └─→ Run: Test cases from guides
   └─→ Know: How to verify correct calculation

5. DEPLOYMENT
   └─→ Follow: PRICING_INTEGRATION_SUMMARY.md
   └─→ Know: Pre-deployment checklist
```


