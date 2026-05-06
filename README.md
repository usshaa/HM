# Hotel Management System

Complete full-stack Hotel Management System built with **Spring Boot 3.2** (backend) and **Angular 17 with NgModule** (frontend).

## Project Structure

```
hotel-management/
├── hotel-management-backend/          # Spring Boot 3.2 Backend
│   ├── src/main/java/com/hotelmanagement/
│   │   ├── config/                    # Configuration classes
│   │   ├── controller/                # REST API Controllers
│   │   ├── dto/                       # Data Transfer Objects
│   │   ├── entity/                    # JPA Entities
│   │   ├── exception/                 # Exception handlers
│   │   ├── repository/                # JPA Repositories
│   │   ├── security/                  # JWT & Security
│   │   ├── service/                   # Business Logic
│   │   └── util/                      # Utility classes
│   ├── src/main/resources/
│   │   ├── db/migration/              # Flyway migrations
│   │   └── application.yml            # Configuration
│   ├── pom.xml                        # Maven dependencies
│   └── Dockerfile                     # Backend container
│
└── hotel-management-frontend/         # Angular 17 Frontend
    ├── src/app/
    │   ├── core/                      # Services, Guards, Interceptors
    │   ├── shared/                    # Models, Components
    │   ├── features/                  # Feature modules
    │   │   ├── auth/                  # Login, Register
    │   │   ├── dashboard/             # Dashboard
    │   │   ├── rooms/                 # Room Management
    │   │   ├── reservations/          # Reservations
    │   │   └── invoices/              # Invoicing
    │   ├── app.module.ts              # Root module
    │   └── app.component.ts           # Root component
    ├── package.json                   # NPM dependencies
    ├── Dockerfile                     # Frontend container
    └── nginx.conf                     # Nginx configuration
```

## Key Features

### Backend (Spring Boot 3.2)
- ✅ **JWT Authentication** with 15-min access tokens and 7-day refresh tokens
- ✅ **Role-Based Access Control** (Admin, Receptionist, Housekeeping, Guest)
- ✅ **Room Management** with status tracking and availability search
- ✅ **Reservation System** with overbooking prevention
- ✅ **Invoice Generation** with GST calculation (12% & 18%)
- ✅ **Housekeeping Management** with task assignment
- ✅ **Audit Logging** for system monitoring
- ✅ **MySQL Database** with Flyway migrations
- ✅ **Exception Handling** with custom error responses
- ✅ **CORS Configuration** for Angular integration

### Frontend (Angular 17 with NgModule)
- ✅ **App Module** with lazy-loaded feature modules
- ✅ **Material Design** components for professional UI
- ✅ **Routing Guards** for authenticated access
- ✅ **HTTP Interceptors** for JWT token injection and error handling
- ✅ **Reactive Forms** with validation
- ✅ **Services** for API communication
- ✅ **TypeScript Models** for type safety
- ✅ **Responsive Design** for desktop and tablet
- ✅ **Role-based Navigation** based on user roles

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Backend Framework | Spring Boot 3.2 |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| Security | Spring Security + JWT |
| Frontend Framework | Angular 17 (NgModule) |
| UI Components | Angular Material |
| Build Tool | Maven (Backend), npm (Frontend) |
| Containerization | Docker + Docker Compose |

## Prerequisites

- Java 17+
- Node.js 18+
- MySQL 8.0+
- Docker & Docker Compose (optional)
- npm or yarn

## Installation & Setup

### Backend Setup

1. **Navigate to backend directory:**
   ```bash
   cd hotel-management-backend
   ```

2. **Configure database (application.yml):**
   ```yaml
   spring.datasource.url=jdbc:mysql://localhost:3306/hotel_management
   spring.datasource.username=root
   spring.datasource.password=root
   ```

3. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   Backend will be available at: `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory:**
   ```bash
   cd hotel-management-frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start development server:**
   ```bash
   npm start
   ```

   Frontend will be available at: `http://localhost:4200`

## Docker Deployment

1. **Build and run with Docker Compose:**
   ```bash
   cd hotel-management
   docker-compose up -d
   ```

2. **Access the application:**
   - Frontend: `http://localhost`
   - Backend API: `http://localhost:8080/api`

3. **Stop containers:**
   ```bash
   docker-compose down
   ```

## API Endpoints

### Authentication
- `POST /auth/register` - Guest registration
- `POST /auth/login` - User login
- `POST /auth/refresh` - Refresh access token

### Rooms
- `GET /rooms` - List all rooms
- `GET /rooms/types` - List room types
- `POST /rooms` - Create room (Admin)
- `GET /rooms/available` - Search available rooms

### Reservations
- `POST /reservations` - Create reservation
- `GET /reservations` - List reservations
- `PATCH /reservations/{id}/confirm` - Confirm reservation
- `POST /reservations/{id}/checkin` - Check-in guest
- `POST /reservations/{id}/checkout` - Check-out guest

### Invoices
- `POST /invoices/generate/{reservationId}` - Generate invoice
- `POST /invoices/{id}/pay` - Record payment
- `GET /invoices/reservation/{reservationId}` - Get invoice

## User Roles & Access

### Admin
- Full system access
- User management
- System configuration
- Financial reports

### Receptionist
- Room availability search
- Reservation management
- Check-in/Check-out operations
- Payment processing

### Housekeeping
- Task list management
- Room status updates
- Maintenance request logging

### Guest
- View own reservations
- Room search and booking
- Invoice download
- Profile management

## Database Schema

### Core Tables
- `users` - User accounts with roles
- `rooms` - Physical rooms
- `room_types` - Room categories
- `reservations` - Guest bookings
- `invoices` - Billing records
- `housekeeping_tasks` - Cleaning tasks
- `maintenance_requests` - Maintenance issues
- `audit_logs` - System audit trail

## Security Features

- ✅ BCrypt password hashing (strength 12)
- ✅ JWT with 15-minute expiration
- ✅ CORS protection
- ✅ Role-based authorization
- ✅ SQL injection prevention (JPA)
- ✅ Audit logging
- ✅ Transactional consistency

## Running Tests

### Backend
```bash
cd hotel-management-backend
mvn test
```

### Frontend
```bash
cd hotel-management-frontend
npm test
```

## Environment Variables

### Backend (.env)
```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/hotel_management
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
GEMINI_API_KEY=your_gemini_api_key
JWT_SECRET=your_super_secret_jwt_key
```

### Frontend (.env)
```
NG_API_URL=http://localhost:8080/api
```

## Troubleshooting

### MySQL Connection Error
- Ensure MySQL is running on port 3306
- Verify credentials in application.yml

### Port Already in Use
```bash
# Find process on port 8080
lsof -i :8080
# Kill process
kill -9 <PID>
```

### Angular Build Error
```bash
npm install --save-dev @angular/cli@latest
npm run build
```

## Performance Optimization

- Database indexes on frequently queried fields
- Pagination on all list endpoints (default: 20 items/page)
- Room availability search <500ms for 1000 rooms
- Angular lazy loading for feature modules

## Future Enhancements

- [ ] Google Maps integration for location
- [ ] Gemini AI for room recommendations
- [ ] Real-time notifications with WebSockets
- [ ] Payment gateway integration (Stripe, PayPal)
- [ ] SMS/Email notifications
- [ ] Mobile app (React Native)
- [ ] Advanced analytics dashboard
- [ ] Multi-language support

## License

This project is open source and available for educational purposes.

## Support

For issues or questions, please refer to the comprehensive API documentation and code comments.

---

**Built with ❤️ for hotel management excellence**
