# Quick Start Guide

## 🚀 Project Overview

This is a **complete, production-ready Hotel Management System** built with:
- **Backend:** Spring Boot 3.2 with JWT authentication and Spring Security
- **Frontend:** Angular 17 with NgModule architecture and Material Design
- **Database:** MySQL 8.0 with Flyway migrations
- **Containerization:** Docker & Docker Compose

## 📋 Prerequisites

- **Java 17+** ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Node.js 18+** ([Download](https://nodejs.org/))
- **MySQL 8.0+** ([Download](https://www.mysql.com/downloads/mysql/))
- **Git** ([Download](https://git-scm.com/))
- **Docker & Docker Compose** (Optional, for containerized deployment)

## 🎯 Quick Start (5 Minutes)

### Step 1: Setup Backend

```bash
cd hotel-management-backend

# Create database
mysql -u root -p
CREATE DATABASE hotel_management;
EXIT;

# Install dependencies and run
mvn clean install
mvn spring-boot:run
```

**Backend runs at:** `http://localhost:8080`

### Step 2: Setup Frontend

```bash
cd hotel-management-frontend

# Install dependencies
npm install

# Start development server
npm start
```

**Frontend runs at:** `http://localhost:4200`

### Step 3: Login

**Test Credentials:**
- Email: `admin@hotel.com` or `receptionist@hotel.com`
- Password: (Set during registration)

## 🐳 Docker Deployment

### One-Command Setup

```bash
docker-compose up -d
```

Access:
- Frontend: `http://localhost`
- API: `http://localhost:8080/api`

### Stop Services

```bash
docker-compose down
```

## 📁 Project Structure

```
hotel-management/
├── hotel-management-backend/
│   ├── src/main/java/com/hotelmanagement/
│   │   ├── config/              # Configuration
│   │   ├── controller/          # REST APIs
│   │   ├── dto/                 # DTOs
│   │   ├── entity/              # JPA Entities
│   │   ├── exception/           # Error Handling
│   │   ├── repository/          # Data Access
│   │   ├── security/            # JWT & Security
│   │   ├── service/             # Business Logic
│   │   └── util/                # Utilities
│   ├── src/main/resources/
│   │   ├── db/migration/        # SQL Scripts (Flyway)
│   │   └── application.yml      # Config
│   ├── pom.xml                  # Maven Deps
│   └── Dockerfile               # Backend Container
│
├── hotel-management-frontend/
│   ├── src/app/
│   │   ├── core/                # Services, Guards, Interceptors
│   │   ├── shared/              # Models, Utilities
│   │   ├── features/            # Feature Modules
│   │   │   ├── auth/            # Login/Register
│   │   │   ├── dashboard/       # Dashboard
│   │   │   ├── rooms/           # Room Management
│   │   │   ├── reservations/    # Bookings
│   │   │   └── invoices/        # Billing
│   │   ├── app.module.ts        # Root Module
│   │   └── app.component.ts     # Root Component
│   ├── package.json             # NPM Deps
│   ├── Dockerfile               # Frontend Container
│   └── nginx.conf               # Nginx Config
│
├── docker-compose.yml           # Container Orchestration
├── README.md                    # Full Documentation
└── QUICKSTART.md               # This file
```

## 🔐 Authentication & Authorization

### JWT Flow

1. **Register/Login** → Backend generates JWT tokens
2. **Access Token** (15 min) → Used for API calls
3. **Refresh Token** (7 days) → Used to get new access token
4. **Interceptor** → Automatically injects Authorization header

### User Roles

| Role | Access |
|------|--------|
| **Admin** | Full system access, user management, reports |
| **Receptionist** | Room search, reservations, check-in/out, payments |
| **Housekeeping** | Task list, room status, maintenance |
| **Guest** | View reservations, book rooms, download invoices |

## 🔌 API Endpoints

### Authentication
```
POST   /auth/register          # Guest registration
POST   /auth/login             # User login
POST   /auth/refresh           # Refresh access token
```

### Rooms
```
GET    /rooms                  # List rooms
GET    /rooms/types            # List room types
POST   /rooms                  # Create room (Admin)
GET    /rooms/available        # Search available rooms
PATCH  /rooms/{id}/status      # Update room status
```

### Reservations
```
POST   /reservations           # Create reservation
GET    /reservations           # List reservations
PATCH  /reservations/{id}/confirm          # Confirm
POST   /reservations/{id}/checkin          # Check-in
POST   /reservations/{id}/checkout         # Check-out
GET    /reservations/guest/{guestId}       # Guest's bookings
```

### Invoices
```
POST   /invoices/generate/{reservationId}  # Generate invoice
POST   /invoices/{id}/pay                  # Record payment
GET    /invoices/reservation/{reservationId}
GET    /invoices
```

## 🧪 Testing the Application

### Backend Testing
```bash
# Unit & Integration Tests
cd hotel-management-backend
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest
```

### Frontend Testing
```bash
# Unit & Component Tests
cd hotel-management-frontend
npm test

# Run tests with coverage
npm run test -- --code-coverage
```

## 📊 Database Schema

### Key Tables
- `users` - User accounts with roles
- `rooms` - Physical rooms in hotel
- `room_types` - Room categories (Single, Double, Suite)
- `reservations` - Guest bookings
- `invoices` - Billing records with GST calculation
- `housekeeping_tasks` - Cleaning and maintenance tasks
- `maintenance_requests` - Maintenance issues
- `audit_logs` - System activity tracking

## 🔒 Security Features

- ✅ **BCrypt Hashing** - Passwords encrypted with strength 12
- ✅ **JWT Tokens** - 15-minute expiration for access tokens
- ✅ **CORS Protection** - Restricted to trusted origins
- ✅ **Role-Based Access** - Method-level security with @PreAuthorize
- ✅ **SQL Injection Prevention** - JPA parameterized queries
- ✅ **Audit Logging** - Track all user actions

## 🛠️ Environment Configuration

### Backend (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hotel_management
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: validate

jwt:
  secret: your_super_secret_key
  expiration: 900000  # 15 minutes
```

### Frontend (environment.ts)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## 🚨 Troubleshooting

### MySQL Connection Failed
```bash
# Verify MySQL is running
mysql -u root -p
SHOW DATABASES;

# Check application.yml credentials
```

### Port 8080 Already in Use (Linux/Mac)
```bash
# Find process
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Port 8080 Already in Use (Windows)
```cmd
# Find process
netstat -ano | findstr :8080

# Kill process
taskkill /PID <PID> /F
```

### Angular Build Errors
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install

# Update Angular CLI
npm install -g @angular/cli@latest
```

### CORS Issues
- Ensure backend has CORS enabled in `CorsConfig.java`
- Check `application.yml` for allowed origins
- Frontend and backend must have matching URL configuration

## 📈 Performance Tips

1. **Database Indexes** - Applied on frequently queried fields
2. **Pagination** - All list endpoints use pagination (default: 20 items)
3. **Lazy Loading** - Angular modules loaded on demand
4. **Caching** - Consider Redis for session management
5. **Connection Pooling** - HikariCP configured in Spring Boot

## 🎓 Learning Resources

### Spring Boot 3.2
- [Spring Boot Official Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)

### Angular 17
- [Angular Official Docs](https://angular.io/docs)
- [Angular Material](https://material.angular.io/)
- [RxJS Documentation](https://rxjs.dev/)

### MySQL
- [MySQL 8.0 Documentation](https://dev.mysql.com/doc/refman/8.0/en/)
- [Flyway Migrations](https://flywaydb.org/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a pull request

## 📝 License

This project is provided as-is for educational and commercial use.

## 💡 Tips & Best Practices

1. **Always backup your database** before migrations
2. **Use environment variables** for sensitive data (JWT secret, API keys)
3. **Follow RESTful conventions** when extending APIs
4. **Write unit tests** for new service methods
5. **Keep dependencies updated** regularly
6. **Use Git branches** for feature development
7. **Document API changes** with comments
8. **Monitor performance** with logging

## 🎯 Next Steps

- [ ] Customize branding (logo, colors)
- [ ] Add email notifications
- [ ] Integrate payment gateway
- [ ] Setup CI/CD pipeline
- [ ] Deploy to production server
- [ ] Configure monitoring & alerts
- [ ] Add advanced analytics
- [ ] Implement mobile app

## 📞 Support & Issues

For issues, questions, or suggestions:
1. Check existing documentation
2. Review code comments
3. Check browser console for errors
4. Check backend logs: `tail -f logs/application.log`

---

**Happy Coding! 🚀**

For full documentation, see [README.md](README.md)
