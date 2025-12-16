-- =====================================================
-- SQL Server Migration Script for Check-in/Check-out Times
-- Hotel Management System - Database Updates
-- =====================================================

-- =====================================================
-- 1. ADD NEW COLUMNS TO RESERVATIONS TABLE
-- =====================================================

-- Add CheckInTime column
IF NOT EXISTS (
    SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'reservations' AND COLUMN_NAME = 'CheckInTime'
)
BEGIN
    ALTER TABLE reservations ADD CheckInTime DATETIME NULL;
    PRINT 'Column CheckInTime added to reservations table';
END
ELSE
BEGIN
    PRINT 'Column CheckInTime already exists in reservations table';
END
GO

-- Add CheckOutTime column
IF NOT EXISTS (
    SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'reservations' AND COLUMN_NAME = 'CheckOutTime'
)
BEGIN
    ALTER TABLE reservations ADD CheckOutTime DATETIME NULL;
    PRINT 'Column CheckOutTime added to reservations table';
END
ELSE
BEGIN
    PRINT 'Column CheckOutTime already exists in reservations table';
END
GO

-- =====================================================
-- 2. VERIFY THE CHANGES
-- =====================================================

-- View the reservations table structure
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'reservations'
ORDER BY ORDINAL_POSITION;
GO

-- =====================================================
-- 3. OPTIONAL: UPDATE EXISTING DATA
-- =====================================================
-- Uncomment and run if you want to set default times for existing checked-in reservations

/*
-- Set CheckInTime for existing Checked-in reservations (using CreatedAt as reference)
UPDATE reservations
SET CheckInTime = DATEADD(HOUR, 14, CAST(CheckInDate AS DATETIME))
WHERE Status = 'Checked-in' AND CheckInTime IS NULL;
GO

-- Set CheckOutTime for existing Checked-out reservations
UPDATE reservations
SET CheckOutTime = DATEADD(HOUR, 12, CAST(CheckOutDate AS DATETIME))
WHERE Status = 'Checked-out' AND CheckOutTime IS NULL;
GO
*/

-- =====================================================
-- 4. SAMPLE DATA - Insert test data (Optional)
-- =====================================================
-- Uncomment to test with sample data

/*
-- Test data with check-in and check-out times
INSERT INTO reservations (CheckInDate, CheckInTime, CheckOutDate, CheckOutTime, NumberOfGuests, TotalAmonut, Status, CreatedAt, GuestId, UserId)
VALUES
(CAST('2025-12-18' AS DATE), '2025-12-18 14:35:00', CAST('2025-12-19' AS DATE), '2025-12-19 12:00:00', 2, 6500000, 'Checked-out', GETDATE(), 1, 1),
(CAST('2025-12-20' AS DATE), '2025-12-20 15:00:00', CAST('2025-12-22' AS DATE), NULL, 3, 8000000, 'Checked-in', GETDATE(), 2, 1);
GO

SELECT * FROM reservations WHERE Status IN ('Checked-in', 'Checked-out');
GO
*/

-- =====================================================
-- 5. VERIFY COLUMN PROPERTIES
-- =====================================================

PRINT '
=== MIGRATION COMPLETE ===
New columns added:
- CheckInTime (DATETIME NULL)
- CheckOutTime (DATETIME NULL)

Your Reservation entity now supports:
✓ Check-in times with precision to minutes
✓ Check-out times with precision to minutes
✓ Full audit trail of when guests checked in/out
';
GO

