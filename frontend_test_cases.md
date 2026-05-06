# Frontend Test Cases — Smart Hotel Management System

> **Total: 80 unique, independent test cases**
> Each test is self-contained with no inter-test dependencies.

---

## 1. AuthService (TC_FE_01 – TC_FE_10)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_01 | `login()` stores tokens in localStorage on success | HttpClientTestingModule configured | Call `login({email, password})`; flush mock response with `success:true, data:{accessToken, refreshToken, user}` | `localStorage.getItem('accessToken')` returns the token; `getCurrentUser()` returns user |
| TC_FE_02 | `login()` updates currentUser$ observable | Subscribe to `currentUser$` | Call `login()` with valid mock response | Subscriber receives user object with correct role |
| TC_FE_03 | `logout()` clears all stored data | User previously logged in (tokens in localStorage) | Call `logout()` | `localStorage.getItem('accessToken')` is null; `getCurrentUser()` returns null |
| TC_FE_04 | `isLoggedIn()` returns true when token exists | accessToken set in localStorage | Call `isLoggedIn()` | Returns `true` |
| TC_FE_05 | `isLoggedIn()` returns false when no token | localStorage empty | Call `isLoggedIn()` | Returns `false` |
| TC_FE_06 | `register()` sends POST to `/auth/register` | HttpClientTestingModule | Call `register({name, email, phone})` | HTTP POST request sent to correct URL with body |
| TC_FE_07 | `refreshToken()` sends refresh token in Authorization header | refreshToken in localStorage | Call `refreshToken()` | HTTP POST to `/auth/refresh` with `Authorization: Bearer <refreshToken>` header |
| TC_FE_08 | `refreshToken()` updates stored accessToken on success | Valid refreshToken stored | Call `refreshToken()`; flush with `{success:true, data: "newToken"}` | `localStorage.getItem('accessToken')` equals "newToken" |
| TC_FE_09 | `getAccessToken()` returns stored token | accessToken="abc123" in localStorage | Call `getAccessToken()` | Returns "abc123" |
| TC_FE_10 | `getUserFromStorage()` parses stored JSON user | Valid JSON user in localStorage | Create new AuthService instance | `getCurrentUser()` returns parsed user object |

---

## 2. AuthGuard (TC_FE_11 – TC_FE_15)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_11 | Allows navigation when user is logged in (no role restriction) | `isLoggedIn()` returns true; route has no `roles` data | Call `canActivate()` | Returns `true` |
| TC_FE_12 | Redirects to `/login` when not logged in | `isLoggedIn()` returns false | Call `canActivate()` with state URL `/dashboard` | Returns `false`; `router.navigate` called with `['/login']` and `queryParams.returnUrl='/dashboard'` |
| TC_FE_13 | Allows navigation when user role matches route roles | User role=ADMIN; route `data.roles=['ADMIN']` | Call `canActivate()` | Returns `true` |
| TC_FE_14 | Redirects to `/unauthorized` when role does not match | User role=GUEST; route `data.roles=['ADMIN']` | Call `canActivate()` | Returns `false`; navigated to `/unauthorized` |
| TC_FE_15 | Preserves returnUrl query parameter on redirect | Not logged in; state URL = `/rooms` | Call `canActivate()` | `router.navigate` called with `queryParams: { returnUrl: '/rooms' }` |

---

## 3. RoleGuard (TC_FE_16 – TC_FE_19)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_16 | Allows access when no required roles specified | User logged in; route `data.roles` undefined | Call `canActivate()` | Returns `true` |
| TC_FE_17 | Allows access when user role is in required list | User role=RECEPTIONIST; route `data.roles=['ADMIN','RECEPTIONIST']` | Call `canActivate()` | Returns `true` |
| TC_FE_18 | Redirects to `/dashboard` when role not in list | User role=GUEST; route `data.roles=['ADMIN']` | Call `canActivate()` | Returns `false`; navigated to `/dashboard` |
| TC_FE_19 | Redirects to `/login` when user is null | `getCurrentUser()` returns null | Call `canActivate()` | Returns `false`; navigated to `/login` |

---

## 4. AuthInterceptor (TC_FE_20 – TC_FE_24)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_20 | Attaches Bearer token to outgoing requests | accessToken="tok123" stored | Make any HTTP request via HttpClient | Request has header `Authorization: Bearer tok123` |
| TC_FE_21 | Does not attach header when no token | No accessToken in localStorage | Make HTTP request | Request has no Authorization header |
| TC_FE_22 | Retries with refreshed token on 401 | First request returns 401; `refreshToken()` succeeds with new token | Make HTTP request | Original request retried with new token |
| TC_FE_23 | Logs out user when refresh also fails | 401 returned; refreshToken() also fails | Make HTTP request | `logout()` called; error propagated |
| TC_FE_24 | Passes through non-401 errors unchanged | Server returns 500 | Make HTTP request | Error observable emits the 500 error unmodified |

---

## 5. ErrorInterceptor (TC_FE_25 – TC_FE_28)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_25 | Extracts server error message from response body | Server returns `{message: "Bad input"}` | Make HTTP request that fails | Error message = "Bad input" |
| TC_FE_26 | Handles client-side ErrorEvent | Network error (ErrorEvent) | Make HTTP request that fails with ErrorEvent | Error message = "Error: <event message>" |
| TC_FE_27 | Fallback message when no error.message | Server returns status 503, no message | Make failing request | Error message contains "Error Code: 503" |
| TC_FE_28 | Logs error to console | Any error response | Make failing request | `console.error` called with error message |

---

## 6. RoomService (TC_FE_29 – TC_FE_33)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_29 | `getRoomTypes()` sends GET with pagination params | HttpClientTestingModule | Call `getRoomTypes(0, 10)` | GET to `/rooms/types?page=0&size=10` |
| TC_FE_30 | `createRoomType()` sends POST with body | HttpClientTestingModule | Call `createRoomType(roomTypeObj)` | POST to `/rooms/types` with roomTypeObj as body |
| TC_FE_31 | `getRooms()` uses default pagination | HttpClientTestingModule | Call `getRooms()` (no args) | GET to `/rooms?page=0&size=20` |
| TC_FE_32 | `updateRoomStatus()` sends PATCH with status param | HttpClientTestingModule | Call `updateRoomStatus(1, 'CLEAN')` | PATCH to `/rooms/1/status?status=CLEAN` |
| TC_FE_33 | `searchAvailableRooms()` passes all query params | HttpClientTestingModule | Call `searchAvailableRooms(1, 1000, 2000)` | GET to `/rooms/available` with roomTypeId, checkInDate, checkOutDate params |

---

## 7. ReservationService (TC_FE_34 – TC_FE_39)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_34 | `createReservation()` sends POST | HttpClientTestingModule | Call with reservation DTO | POST to `/reservations` with DTO body |
| TC_FE_35 | `confirmReservation()` sends PATCH | HttpClientTestingModule | Call `confirmReservation(5)` | PATCH to `/reservations/5/confirm` |
| TC_FE_36 | `checkIn()` sends POST with roomId param | HttpClientTestingModule | Call `checkIn(5, 10)` | POST to `/reservations/5/checkin?roomId=10` |
| TC_FE_37 | `checkOut()` sends POST to correct URL | HttpClientTestingModule | Call `checkOut(5)` | POST to `/reservations/5/checkout` |
| TC_FE_38 | `getAllReservations()` default pagination | HttpClientTestingModule | Call `getAllReservations()` | GET to `/reservations?page=0&size=20` |
| TC_FE_39 | `getGuestReservations()` includes guestId in URL | HttpClientTestingModule | Call `getGuestReservations(7)` | GET to `/reservations/guest/7?page=0&size=20` |

---

## 8. InvoiceService (TC_FE_40 – TC_FE_44)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_40 | `generateInvoice()` sends POST | HttpClientTestingModule | Call `generateInvoice(3)` | POST to `/invoices/generate/3` |
| TC_FE_41 | `recordPayment()` sends amount as param | HttpClientTestingModule | Call `recordPayment(1, 500)` | POST to `/invoices/1/pay?amount=500` |
| TC_FE_42 | `getInvoiceByReservation()` correct URL | HttpClientTestingModule | Call `getInvoiceByReservation(3)` | GET to `/invoices/reservation/3` |
| TC_FE_43 | `getAllInvoices()` sends pagination params | HttpClientTestingModule | Call `getAllInvoices(2, 10)` | GET to `/invoices?page=2&size=10` |
| TC_FE_44 | `getAllInvoices()` default pagination | HttpClientTestingModule | Call `getAllInvoices()` | GET to `/invoices?page=0&size=20` |

---

## 9. HousekeepingService (TC_FE_45 – TC_FE_50)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_45 | `getTasks()` without filter sends no assignedTo param | HttpClientTestingModule | Call `getTasks()` | GET to `/housekeeping/tasks` with no assignedTo param |
| TC_FE_46 | `getTasks()` with assignedTo filter | HttpClientTestingModule | Call `getTasks(5)` | GET to `/housekeeping/tasks?assignedTo=5` |
| TC_FE_47 | `createTask()` sends POST with body | HttpClientTestingModule | Call `createTask({roomId:1, assignedToId:5, taskType:'DAILY_SERVICE'})` | POST to `/housekeeping/tasks` |
| TC_FE_48 | `updateTaskStatus()` sends PATCH with status in URL | HttpClientTestingModule | Call `updateTaskStatus(3, 'COMPLETED')` | PATCH to `/housekeeping/tasks/3/status?status=COMPLETED` |
| TC_FE_49 | `createMaintenanceRequest()` sends POST | HttpClientTestingModule | Call with maintenance DTO | POST to `/housekeeping/maintenance` |
| TC_FE_50 | `getMaintenanceRequests()` with status filter | HttpClientTestingModule | Call `getMaintenanceRequests('OPEN')` | GET to `/housekeeping/maintenance?status=OPEN` |

---

## 10. DashboardComponent (TC_FE_51 – TC_FE_57)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_51 | Component creates successfully | Module imports configured | Create component via TestBed | Component instance is truthy |
| TC_FE_52 | `ngOnInit` calls all 4 load methods | Spy on loadKpis, loadTrendChart, loadBookingSources, loadRevenueChart | Trigger ngOnInit | All 4 methods called once each |
| TC_FE_53 | `loadKpis()` sets occupancyRate from API response | Mock AnalyticsService returns occupancy data | Call loadKpis() | `component.occupancyRate` equals mocked value |
| TC_FE_54 | `loadKpis()` uses fallback values on API error | AnalyticsService.getOccupancy returns error | Call loadKpis() | `occupancyRate=75`, `availableRooms=8`, `totalRooms=20` |
| TC_FE_55 | `loadTrendChart()` populates lineChartData | Mock trend API returns labels and rates | Call loadTrendChart() | `lineChartData.labels` has correct values |
| TC_FE_56 | `loadBookingSources()` populates pieChartData | Mock booking source API returns data | Call loadBookingSources() | `pieChartData.labels` contains source names |
| TC_FE_57 | Loading flag set to false after KPI load | Initially loading=true | loadKpis() completes | `component.loading` is `false` |

---

## 11. ProfileComponent (TC_FE_58 – TC_FE_63)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_58 | Profile form initialised with current user data | currentUser = `{name: "John", phone: "123"}` | ngOnInit runs | `profileForm.get('name').value` equals "John" |
| TC_FE_59 | Password form has required validators | Component created | Check `passwordForm.get('currentPassword')` without value | Form control is invalid |
| TC_FE_60 | New password minimum length validation | Component created | Set newPassword to "abc" (< 8 chars) | `passwordForm.get('newPassword')` has minlength error |
| TC_FE_61 | `saveProfile()` shows snackbar on valid form | profileForm is valid | Call `saveProfile()` | `MatSnackBar.open` called with "Profile updated successfully!" |
| TC_FE_62 | `changePassword()` shows mismatch error | newPassword ≠ confirmPassword | Call `changePassword()` | Snackbar shows "Passwords do not match!" |
| TC_FE_63 | `changePassword()` resets form on success | Passwords match and form valid | Call `changePassword()` | `passwordForm` is reset; snackbar shows success |

---

## 12. InsightsComponent (TC_FE_64 – TC_FE_68)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_64 | Loads recommendations on init | Mock InsightService returns JSON array string | ngOnInit | `recommendations` array has 3 items |
| TC_FE_65 | Handles non-JSON recommendation response | InsightService returns plain text | ngOnInit | `recommendations` has 1 item with category='GENERAL' |
| TC_FE_66 | Loading flag for recommendations toggles | Initially loadingRec=true | After loadRecommendations completes | `loadingRec` is false |
| TC_FE_67 | `getCategoryIcon()` returns correct icon | N/A | Call `getCategoryIcon('ROOM_UPGRADE')` | Returns 'upgrade' |
| TC_FE_68 | `getCategoryIcon()` returns fallback for unknown | N/A | Call `getCategoryIcon('UNKNOWN')` | Returns 'lightbulb' |

---

## 13. AppRoutingModule (TC_FE_69 – TC_FE_74)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_69 | Root path redirects to `/dashboard` | App initialised | Navigate to `/` | URL becomes `/dashboard` |
| TC_FE_70 | `/rooms` route restricted to ADMIN, RECEPTIONIST | Route config loaded | Check route data for `/rooms` | `data.roles` = `['ADMIN', 'RECEPTIONIST']` |
| TC_FE_71 | `/admin` route restricted to ADMIN only | Route config loaded | Check route data for `/admin` | `data.roles` = `['ADMIN']` |
| TC_FE_72 | `/housekeeping` accessible to HOUSEKEEPING role | Route config loaded | Check route data | `data.roles` includes 'HOUSEKEEPING' |
| TC_FE_73 | Wildcard `**` redirects to `/dashboard` | App initialised | Navigate to `/nonexistent` | URL becomes `/dashboard` |
| TC_FE_74 | All protected routes have AuthGuard | Route config loaded | Inspect all routes except auth | `canActivate` includes `AuthGuard` |

---

## 14. Shared Models & AppComponent (TC_FE_75 – TC_FE_80)

| ID | Test Case | Preconditions | Steps | Expected Result |
|----|-----------|---------------|-------|-----------------|
| TC_FE_75 | `UserRole` enum has exactly 4 values | N/A | Check enum keys | ADMIN, RECEPTIONIST, HOUSEKEEPING, GUEST |
| TC_FE_76 | `RoomStatus` enum has 8 values | N/A | Check enum keys | AVAILABLE, RESERVED, OCCUPIED, DIRTY, CLEANING, CLEAN, INSPECTION, MAINTENANCE |
| TC_FE_77 | `ReservationStatus` enum has 6 values | N/A | Check enum keys | PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED, NO_SHOW |
| TC_FE_78 | `PaymentMode` enum includes UPI and SPLIT | N/A | Check enum keys | Contains CASH, CARD, UPI, SPLIT |
| TC_FE_79 | `ApiResponse` interface has success, message, optional data and errors | N/A | Create object conforming to interface | Object compiles with success, message, data?, errors? |
| TC_FE_80 | AppComponent renders navigation sidebar with correct links | Component created, user logged in as ADMIN | Inspect rendered HTML | Sidebar contains links for Dashboard, Rooms, Reservations, Invoices, Housekeeping, Admin, Insights, Profile |
