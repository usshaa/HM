# API Documentation

## Base URL

```
http://localhost:8080/api
```

## Authentication

All endpoints (except `/auth/register`, `/auth/login`, `/auth/health`) require JWT token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ...
```

### Token Expiration

- **Access Token:** 15 minutes
- **Refresh Token:** 7 days

### Token Refresh

When access token expires, use the refresh token to get a new one:

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer <refresh_token>"
```

## Response Format

All API responses follow this format:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* data object */ },
  "errors": []
}
```

### Error Response

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "errors": ["Detailed error message"]
}
```

## Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Success |
| 201 | Created - Resource created |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Missing/invalid token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 500 | Server Error |

## Endpoints

### Authentication Endpoints

#### 1. Register (Create Guest Account)

```http
POST /auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+971501234567",
  "password": "SecurePass123!"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "email": "john@example.com",
    "name": "John Doe",
    "role": "GUEST"
  }
}
```

#### 2. Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "email": "john@example.com",
      "name": "John Doe",
      "role": "GUEST"
    }
  }
}
```

#### 3. Refresh Token

```http
POST /auth/refresh
Authorization: Bearer <refresh_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token refreshed",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

#### 4. Change Password

```http
POST /auth/change-password
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "oldPassword": "OldPass123!",
  "newPassword": "NewPass456!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Password changed successfully"
}
```

#### 5. Health Check

```http
GET /auth/health
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Service is running",
  "data": "OK"
}
```

---

### Room Management Endpoints

#### 1. Get All Room Types

```http
GET /rooms/types?page=0&size=10
Authorization: Bearer <access_token>
```

**Query Parameters:**
- `page`: Page number (0-indexed)
- `size`: Items per page

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "typeName": "Single Room",
        "basePrice": 500.00,
        "maxOccupancy": 1,
        "bedType": "SINGLE",
        "amenities": ["WiFi", "AC", "TV"]
      }
    ],
    "totalElements": 3,
    "totalPages": 1
  }
}
```

#### 2. Create Room Type (Admin Only)

```http
POST /rooms/types
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "typeName": "Deluxe Suite",
  "description": "Premium room with luxury amenities",
  "basePrice": 1200.00,
  "maxOccupancy": 4,
  "bedType": "KING",
  "amenities": ["WiFi", "AC", "TV", "Jacuzzi"],
  "photoUrl": "https://example.com/photo.jpg"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Room type created",
  "data": {
    "id": 4,
    "typeName": "Deluxe Suite"
  }
}
```

#### 3. Get All Rooms

```http
GET /rooms?page=0&size=20
Authorization: Bearer <access_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "roomNumber": "101",
        "floor": 1,
        "status": "AVAILABLE",
        "roomType": {
          "id": 1,
          "typeName": "Single Room",
          "basePrice": 500.00
        }
      }
    ],
    "totalElements": 50
  }
}
```

#### 4. Create Room (Admin Only)

```http
POST /rooms
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "roomNumber": "501",
  "floor": 5,
  "roomTypeId": 1,
  "notes": "Recently renovated"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Room created",
  "data": {
    "id": 51,
    "roomNumber": "501"
  }
}
```

#### 5. Update Room Status

```http
PATCH /rooms/1/status
Authorization: Bearer <admin_or_receptionist_token>
Content-Type: application/json

{
  "status": "MAINTENANCE"
}
```

**Valid Status Values:**
- AVAILABLE
- RESERVED
- OCCUPIED
- DIRTY
- CLEANING
- CLEAN
- INSPECTION
- MAINTENANCE

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Room status updated"
}
```

#### 6. Search Available Rooms

```http
GET /rooms/available?roomTypeId=1&checkInDate=1640000000&checkOutDate=1640200000
Authorization: Bearer <access_token>
```

**Query Parameters:**
- `roomTypeId`: Room type ID
- `checkInDate`: Check-in date (milliseconds since epoch)
- `checkOutDate`: Check-out date (milliseconds since epoch)

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "roomNumber": "101",
      "basePrice": 500.00
    },
    {
      "id": 2,
      "roomNumber": "102",
      "basePrice": 500.00
    }
  ]
}
```

---

### Reservation Endpoints

#### 1. Create Reservation

```http
POST /reservations
Authorization: Bearer <receptionist_or_guest_token>
Content-Type: application/json

{
  "guestId": 5,
  "roomTypeId": 1,
  "checkInDate": 1640000000000,
  "checkOutDate": 1640200000000,
  "numAdults": 2,
  "numChildren": 1,
  "source": "PORTAL",
  "specialRequests": "High floor preferred",
  "earlyCheckInRequested": false,
  "lateCheckOutRequested": true
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Reservation created",
  "data": {
    "id": 1,
    "status": "PENDING",
    "totalAmount": 4000.00
  }
}
```

#### 2. Get All Reservations

```http
GET /reservations?page=0&size=10
Authorization: Bearer <access_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "guestName": "John Doe",
        "roomNumber": "101",
        "checkInDate": 1640000000000,
        "checkOutDate": 1640200000000,
        "status": "PENDING",
        "totalAmount": 4000.00
      }
    ]
  }
}
```

#### 3. Get Guest Reservations

```http
GET /reservations/guest/5?page=0&size=10
Authorization: Bearer <access_token>
```

**Response:** Same format as "Get All Reservations"

#### 4. Confirm Reservation

```http
PATCH /reservations/1/confirm
Authorization: Bearer <receptionist_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Reservation confirmed"
}
```

#### 5. Check-In Guest

```http
POST /reservations/1/checkin
Authorization: Bearer <receptionist_token>
Content-Type: application/json

{
  "roomId": 1
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Guest checked in successfully"
}
```

#### 6. Check-Out Guest

```http
POST /reservations/1/checkout
Authorization: Bearer <receptionist_token>
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Guest checked out successfully"
}
```

---

### Invoice Endpoints

#### 1. Generate Invoice

```http
POST /invoices/generate/1
Authorization: Bearer <receptionist_token>
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Invoice generated",
  "data": {
    "id": 1,
    "reservationId": 1,
    "roomCharges": 4000.00,
    "extraCharges": 500.00,
    "taxAmount": 810.00,
    "totalAmount": 5310.00,
    "paidAmount": 0,
    "balanceDue": 5310.00,
    "paymentStatus": "UNPAID"
  }
}
```

#### 2. Record Payment

```http
POST /invoices/1/pay
Authorization: Bearer <receptionist_token>
Content-Type: application/json

{
  "amount": 5310.00,
  "paymentMode": "CARD"
}
```

**Valid Payment Modes:**
- CASH
- CARD
- UPI
- SPLIT

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Payment recorded",
  "data": {
    "id": 1,
    "paidAmount": 5310.00,
    "balanceDue": 0.00,
    "paymentStatus": "PAID"
  }
}
```

#### 3. Get Invoice

```http
GET /invoices/1
Authorization: Bearer <receptionist_or_guest_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "reservationId": 1,
    "roomCharges": 4000.00,
    "extraCharges": 500.00,
    "discountAmount": 0.00,
    "taxAmount": 810.00,
    "totalAmount": 5310.00,
    "paidAmount": 5310.00,
    "balanceDue": 0.00,
    "paymentMode": "CARD",
    "paymentStatus": "PAID"
  }
}
```

#### 4. Get Invoice by Reservation

```http
GET /invoices/reservation/1
Authorization: Bearer <receptionist_or_guest_token>
```

**Response:** Same format as "Get Invoice"

#### 5. Get All Invoices

```http
GET /invoices?page=0&size=20
Authorization: Bearer <receptionist_or_admin_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "reservationId": 1,
        "totalAmount": 5310.00,
        "paymentStatus": "PAID"
      }
    ],
    "totalElements": 15
  }
}
```

---

## Error Examples

### Missing Authorization Header

```bash
curl http://localhost:8080/api/rooms
```

**Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Unauthorized",
  "errors": ["Access Denied"]
}
```

### Invalid Credentials

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"wrong@example.com","password":"wrong"}'
```

**Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid email or password",
  "errors": []
}
```

### Resource Not Found

```bash
curl http://localhost:8080/api/rooms/999 \
  -H "Authorization: Bearer <token>"
```

**Response (404 Not Found):**
```json
{
  "success": false,
  "message": "Room not found",
  "errors": ["Resource with ID 999 not found"]
}
```

### Insufficient Permissions

```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Authorization: Bearer <guest_token>" \
  -H "Content-Type: application/json" \
  -d '{...}'
```

**Response (403 Forbidden):**
```json
{
  "success": false,
  "message": "Access Denied",
  "errors": ["You do not have permission to access this resource"]
}
```

---

## Testing with cURL

### Example: Complete Booking Flow

```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name":"John Doe",
    "email":"john@example.com",
    "phone":"+971501234567",
    "password":"Pass123!"
  }'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email":"john@example.com",
    "password":"Pass123!"
  }'

# 3. Get available rooms
curl "http://localhost:8080/api/rooms/available?roomTypeId=1&checkInDate=1640000000&checkOutDate=1640200000" \
  -H "Authorization: Bearer <access_token>"

# 4. Create reservation
curl -X POST http://localhost:8080/api/reservations \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "guestId":1,
    "roomTypeId":1,
    "checkInDate":1640000000000,
    "checkOutDate":1640200000000,
    "numAdults":2,
    "numChildren":1,
    "source":"PORTAL"
  }'

# 5. Generate invoice
curl -X POST http://localhost:8080/api/invoices/generate/1 \
  -H "Authorization: Bearer <receptionist_token>"

# 6. Record payment
curl -X POST http://localhost:8080/api/invoices/1/pay \
  -H "Authorization: Bearer <receptionist_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "amount":5310.00,
    "paymentMode":"CARD"
  }'
```

---

## Rate Limiting

Currently no rate limiting is implemented. For production, consider implementing:
- Request throttling (e.g., 100 requests/minute per user)
- IP-based limiting
- Token bucket algorithm

---

## Pagination

All list endpoints support pagination:

```
GET /endpoint?page=0&size=20
```

**Response:**
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0,
  "pageSize": 20
}
```

---

## Date Format

Dates are transmitted as **milliseconds since epoch (Unix timestamp)**.

```javascript
// JavaScript example
const checkInDate = new Date('2024-12-20').getTime(); // 1703011200000
```

---

## Useful Tools

### Postman

Import the collection from backend for easier API testing.

### Insomnia

Alternative REST client with similar features to Postman.

### API Blueprint / Swagger

Consider generating Swagger/OpenAPI documentation:

```bash
mvn swagger2markup:convertSwagger
```

---

**Happy API Testing! 🚀**
