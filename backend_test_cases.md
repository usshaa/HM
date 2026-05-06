# Backend Test Cases — Smart Hotel Management System

> **Total: 80 unique, independent test cases**
> Each test is self-contained with no inter-test dependencies.

---

## 1. Authentication — AuthController / AuthService (TC_BE_01 – TC_BE_12)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_BE_01 | Register a new guest with valid data | No user exists with email `test@mail.com` | POST `/auth/register` with `{email, name, phone}` | 201 Created; response contains `success: true`, user DTO with role=GUEST |
| TC_BE_02 | Register with duplicate email | User `test@mail.com` already exists | POST `/auth/register` with same email | 400 Bad Request; message "Email already registered" |
| TC_BE_03 | Login with valid credentials | Registered user exists | POST `/auth/login` with `{email, password}` | 200 OK; response contains accessToken, refreshToken, user DTO, tokenType="Bearer" |
| TC_BE_04 | Login with wrong password | Registered user exists | POST `/auth/login` with incorrect password | 400 Bad Request; message "Invalid email or password" |
| TC_BE_05 | Login with non-existent email | No user with given email | POST `/auth/login` with unknown email | 400 Bad Request; message "Invalid email or password" |
| TC_BE_06 | Refresh token with valid refresh token | User logged in, valid refresh token available | POST `/auth/refresh` with `Authorization: Bearer <refreshToken>` | 200 OK; new access token string returned |
| TC_BE_07 | Refresh token with invalid token | N/A | POST `/auth/refresh` with `Authorization: Bearer invalidtoken` | 400 Bad Request; message "Invalid refresh token" |
| TC_BE_08 | Change password with correct old password | User exists with known password | POST `/auth/change-password?email=…&oldPassword=…&newPassword=…` | 200 OK; message "Password changed successfully" |
| TC_BE_09 | Change password with incorrect old password | User exists | POST `/auth/change-password` with wrong oldPassword | 400 Bad Request; message "Old password is incorrect" |
| TC_BE_10 | Change password for non-existent user | No user with given email | POST `/auth/change-password?email=unknown@x.com&…` | 404 Not Found; message "User not found" |
| TC_BE_11 | Health check endpoint | Server running | GET `/auth/health` | 200 OK; `success: true`, data="OK" |
| TC_BE_12 | Register guest sets default currency AED and firstLoginPasswordChangeRequired=true | No user with email | POST `/auth/register` | 201; saved user has preferredCurrency="AED", firstLoginPasswordChangeRequired=true |

---

## 2. Room Management — RoomController / RoomService (TC_BE_13 – TC_BE_24)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_BE_13 | Get all room types (paginated) | At least 1 active room type exists | GET `/rooms/types?page=0&size=10` | 200 OK; paginated list of active room types |
| TC_BE_14 | Create room type as ADMIN | Authenticated as ADMIN | POST `/rooms/types` with valid `{typeName, basePrice, maxOccupancy, bedType}` | 201 Created; room type DTO returned |
| TC_BE_15 | Create room type as non-ADMIN | Authenticated as RECEPTIONIST | POST `/rooms/types` with valid body | 403 Forbidden |
| TC_BE_16 | Update room type as ADMIN | Room type id=1 exists | PUT `/rooms/types/1` with `{typeName: "Deluxe Updated"}` | 200 OK; updated room type DTO |
| TC_BE_17 | Delete (deactivate) room type | Room type id=1 exists, active | DELETE `/rooms/types/1` as ADMIN | 200 OK; room type isActive=false |
| TC_BE_18 | Delete non-existent room type | No room type id=999 | DELETE `/rooms/types/999` as ADMIN | 404 Not Found; "Room type not found" |
| TC_BE_19 | Get all rooms (paginated) | At least 1 active room exists | GET `/rooms?page=0&size=20` | 200 OK; paginated room list |
| TC_BE_20 | Create room as ADMIN | Room type exists | POST `/rooms` with `{roomNumber, floor, roomTypeId}` | 201 Created; room DTO with status=AVAILABLE |
| TC_BE_21 | Create room with invalid room type | No room type id=999 | POST `/rooms` with roomTypeId=999 | 404 Not Found; "Room type not found" |
| TC_BE_22 | Update room status to CLEAN | Room id=1 exists | PATCH `/rooms/1/status?status=CLEAN` as RECEPTIONIST | 200 OK; status=CLEAN, lastCleaned timestamp set |
| TC_BE_23 | Soft-delete room | Room id=1 exists | DELETE `/rooms/1` as ADMIN | 200 OK; room isActive=false |
| TC_BE_24 | Search available rooms by type and dates | Rooms exist with status=AVAILABLE | GET `/rooms/available?roomTypeId=1&checkInDate=…&checkOutDate=…` | 200 OK; list of available rooms of that type |

---

## 3. Reservation Management — ReservationController / ReservationService (TC_BE_25 – TC_BE_40)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_BE_25 | Create reservation with valid data | Guest and room type exist, rooms available | POST `/reservations` with valid DTO | 201 Created; status=PENDING, totalAmount calculated |
| TC_BE_26 | Create reservation with checkOut ≤ checkIn | Guest and room type exist | POST `/reservations` with checkOutDate ≤ checkInDate | 400 Bad Request; "Check-out date must be after check-in date" |
| TC_BE_27 | Create reservation with non-existent guest | No guest id=999 | POST `/reservations` with guestId=999 | 404 Not Found; "Guest not found" |
| TC_BE_28 | Create reservation with non-existent room type | No room type id=999 | POST `/reservations` with roomTypeId=999 | 404 Not Found; "Room type not found" |
| TC_BE_29 | Create reservation when no rooms available | All rooms occupied/reserved | POST `/reservations` | 400 Bad Request; "No rooms available for selected type and dates" |
| TC_BE_30 | Modify a PENDING reservation | Reservation id=1, status=PENDING | PUT `/reservations/1` with updated dates | 200 OK; updated DTO, recalculated totalAmount |
| TC_BE_31 | Modify a CHECKED_IN reservation | Reservation status=CHECKED_IN | PUT `/reservations/{id}` | 400 Bad Request; "Cannot modify a checked-in or checked-out reservation" |
| TC_BE_32 | Confirm a PENDING reservation | Reservation status=PENDING | PATCH `/reservations/{id}/confirm` | 200 OK; status=CONFIRMED |
| TC_BE_33 | Confirm a non-PENDING reservation | Reservation status=CONFIRMED | PATCH `/reservations/{id}/confirm` | 400 Bad Request; "Only pending reservations can be confirmed" |
| TC_BE_34 | Cancel a PENDING reservation | Reservation status=PENDING | PATCH `/reservations/{id}/cancel` | 200 OK; status=CANCELLED |
| TC_BE_35 | Cancel a CHECKED_IN reservation | Reservation status=CHECKED_IN | PATCH `/reservations/{id}/cancel` | 400 Bad Request; "Cannot cancel a checked-in reservation" |
| TC_BE_36 | Cancel an already CANCELLED reservation | Reservation status=CANCELLED | PATCH `/reservations/{id}/cancel` | 400 Bad Request; "Reservation is already cancelled" |
| TC_BE_37 | Check-in guest with valid room | Reservation CONFIRMED, room AVAILABLE | POST `/reservations/{id}/checkin?roomId=1` | 200 OK; status=CHECKED_IN, room status=OCCUPIED |
| TC_BE_38 | Check-in a cancelled reservation | Reservation CANCELLED | POST `/reservations/{id}/checkin?roomId=1` | 400 Bad Request; "Cannot check in a cancelled reservation" |
| TC_BE_39 | Check-out guest | Reservation CHECKED_IN | POST `/reservations/{id}/checkout` | 200 OK; status=CHECKED_OUT, room status=DIRTY |
| TC_BE_40 | Check-out a non-checked-in reservation | Reservation PENDING | POST `/reservations/{id}/checkout` | 400 Bad Request; "Only checked-in reservations can be checked out" |

---

## 4. Reservation Queries & Export (TC_BE_41 – TC_BE_44)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_BE_41 | Get reservation by ID | Reservation id=1 exists | GET `/reservations/1` | 200 OK; reservation DTO returned |
| TC_BE_42 | Get non-existent reservation | No reservation id=999 | GET `/reservations/999` | 404 Not Found; "Reservation not found" |
| TC_BE_43 | Get guest reservations | Guest has reservations | GET `/reservations/guest/{guestId}` | 200 OK; paginated list of guest's reservations |
| TC_BE_44 | Export reservations as CSV | Reservations exist | GET `/reservations/export/csv` as ADMIN | 200 OK; Content-Type=text/csv, CSV header row present |

---

## 5. Invoice Management — InvoiceController / InvoiceService (TC_BE_45 – TC_BE_56)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_BE_45 | Generate invoice for reservation | Reservation exists, no invoice yet | POST `/invoices/generate/{reservationId}` as RECEPTIONIST | 201 Created; invoice DTO with paymentStatus=UNPAID |
| TC_BE_46 | Generate duplicate invoice | Invoice already exists for reservation | POST `/invoices/generate/{reservationId}` | 400 Bad Request; "Invoice already exists for this reservation" |
| TC_BE_47 | Generate invoice for non-existent reservation | No reservation id=999 | POST `/invoices/generate/999` | 404 Not Found; "Reservation not found" |
| TC_BE_48 | Record payment with valid amount | Invoice exists, totalAmount=1000, paidAmount=0 | POST `/invoices/{id}/pay?amount=500` | 200 OK; paidAmount=500, balanceDue=500, paymentStatus=PARTIAL |
| TC_BE_49 | Record full payment | Invoice totalAmount=1000, paidAmount=0 | POST `/invoices/{id}/pay?amount=1000` | 200 OK; paymentStatus=PAID, balanceDue=0 |
| TC_BE_50 | Record payment exceeding total | Invoice totalAmount=1000 | POST `/invoices/{id}/pay?amount=1500` | 400 Bad Request; "Payment exceeds invoice total" |
| TC_BE_51 | Record payment with zero amount | Invoice exists | POST `/invoices/{id}/pay?amount=0` | 400 Bad Request; "Payment amount must be greater than 0" |
| TC_BE_52 | Record payment with specific PaymentMode | Invoice exists | POST `/invoices/{id}/pay?amount=100&mode=CARD` | 200 OK; paymentMode=CARD |
| TC_BE_53 | Process refund | Invoice exists | POST `/invoices/{id}/refund` | 200 OK; paymentStatus=REFUNDED, balanceDue=0 |
| TC_BE_54 | Get invoice by ID | Invoice id=1 exists | GET `/invoices/1` | 200 OK; invoice DTO |
| TC_BE_55 | Get invoice by reservation ID | Invoice linked to reservation | GET `/invoices/reservation/{reservationId}` | 200 OK; invoice DTO |
| TC_BE_56 | Export invoice as PDF | Invoice exists | GET `/invoices/{id}/export/pdf` | 200 OK; Content-Type=application/pdf, non-empty byte array |

---

## 6. Invoice Tax Calculation (TC_BE_57 – TC_BE_58)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_BE_57 | Tax rate 12% for subtotal < 5000 | Room charges + extras < 5000 | Generate invoice | taxAmount = subtotal × 0.12 |
| TC_BE_58 | Tax rate 18% for subtotal ≥ 5000 | Room charges + extras ≥ 5000 | Generate invoice | taxAmount = subtotal × 0.18 |

---

## 7. Housekeeping — HousekeepingController / HousekeepingService (TC_BE_59 – TC_BE_68)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_BE_59 | Create housekeeping task | Room exists, HOUSEKEEPING staff exists | POST `/housekeeping/tasks` with valid DTO | 201 Created; status=PENDING |
| TC_BE_60 | Create task assigned to non-housekeeping user | Staff role=RECEPTIONIST | POST `/housekeeping/tasks` with that staff ID | 400 Bad Request; "Tasks can only be assigned to housekeeping staff" |
| TC_BE_61 | Create task for non-existent room | No room id=999 | POST `/housekeeping/tasks` with roomId=999 | 404 Not Found; "Room not found" |
| TC_BE_62 | Update task status to COMPLETED | Task exists, type=DAILY_SERVICE | PATCH `/housekeeping/tasks/{id}/status?status=COMPLETED` | 200 OK; status=COMPLETED, completedAt set, room status=CLEAN |
| TC_BE_63 | Get tasks filtered by assignedTo | Tasks exist for staff id=5 | GET `/housekeeping/tasks?assignedTo=5` | 200 OK; only tasks assigned to staff 5 |
| TC_BE_64 | Get all tasks without filter | Tasks exist | GET `/housekeeping/tasks` | 200 OK; paginated list of all tasks |
| TC_BE_65 | Create maintenance request | Room exists, reporter exists | POST `/housekeeping/maintenance` with valid DTO | 201 Created; status=OPEN, room status=MAINTENANCE |
| TC_BE_66 | Create maintenance for non-existent room | No room id=999 | POST `/housekeeping/maintenance` with roomId=999 | 404 Not Found; "Room not found" |
| TC_BE_67 | Resolve maintenance request | Maintenance request exists | PATCH `/housekeeping/maintenance/{id}/status?status=RESOLVED` | 200 OK; status=RESOLVED, resolvedAt set, room status=AVAILABLE |
| TC_BE_68 | Get maintenance requests filtered by status | Maintenance requests exist | GET `/housekeeping/maintenance?status=OPEN` | 200 OK; only OPEN maintenance requests |

---

## 8. Admin — AdminController / AdminService (TC_BE_69 – TC_BE_74)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_BE_69 | Get all users as ADMIN | Users exist | GET `/admin/users` as ADMIN | 200 OK; paginated user list |
| TC_BE_70 | Get all users as non-ADMIN | Authenticated as GUEST | GET `/admin/users` | 403 Forbidden |
| TC_BE_71 | Deactivate user | User id=5 active | PATCH `/admin/users/5/activate?activate=false` | 200 OK; user isActive=false |
| TC_BE_72 | Create staff account | No user with given email | POST `/admin/staff` with role=RECEPTIONIST | 201 Created; user with role=RECEPTIONIST, default password set |
| TC_BE_73 | Create staff with GUEST role | N/A | POST `/admin/staff` with role=GUEST | 400 Bad Request; "Cannot create a GUEST account through admin panel" |
| TC_BE_74 | Get audit logs with date range | Audit logs exist | GET `/admin/audit-logs?from=…&to=…` as ADMIN | 200 OK; filtered audit logs within range |

---

## 9. Analytics — AnalyticsController / AnalyticsService (TC_BE_75 – TC_BE_79)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_BE_75 | Get daily occupancy data | Rooms exist | GET `/analytics/occupancy?period=daily` | 200 OK; list with single "Today" entry, occupancyRate calculated |
| TC_BE_76 | Get weekly occupancy data | Rooms exist | GET `/analytics/occupancy?period=weekly` | 200 OK; list with 7 entries (day labels) |
| TC_BE_77 | Get revenue data for specific month/year | Invoices exist | GET `/analytics/revenue?month=5&year=2026` | 200 OK; RevenueData with totalRevenue, byType map, byMode map |
| TC_BE_78 | Get KPI data | Reservations and rooms exist | GET `/analytics/kpi` as ADMIN | 200 OK; KpiData with ADR, RevPAR, avgStay, occupancy |
| TC_BE_79 | Get booking source distribution | Reservations exist | GET `/analytics/booking-sources` as ADMIN | 200 OK; list of BookingSourceData with source, count, percentage |

---

## 10. Insights & Security (TC_BE_80)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_BE_80 | Get room recommendations (mock fallback) | Gemini API key not configured | POST `/insights/room-recommendations` with `{guestProfile: "Business traveler"}` | 200 OK; JSON array with 3 mock recommendations (Suite Upgrade, Spa, Desert Safari) |
