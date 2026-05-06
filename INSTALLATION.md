# Hotel Management System - Installation Guide

## Complete Setup Instructions

### Prerequisites Check

Before starting, verify you have all required software:

```bash
# Check Java version (should be 17+)
java -version

# Check Node.js version (should be 18+)
node -v

# Check npm version
npm -v

# Check MySQL version
mysql --version
```

## Part 1: Database Setup

### Option A: Manual MySQL Setup

```bash
# Open MySQL
mysql -u root -p

# Create database
CREATE DATABASE hotel_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Create admin user (optional)
CREATE USER 'hotel_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON hotel_management.* TO 'hotel_user'@'localhost';
FLUSH PRIVILEGES;

EXIT;
```

### Option B: Using Docker

```bash
# Run MySQL container
docker run --name hotel_mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=hotel_management \
  -p 3306:3306 \
  -v mysql_data:/var/lib/mysql \
  -d mysql:8.0
```

## Part 2: Backend Setup

### 1. Navigate to Backend Directory

```bash
cd hotel-management-backend
```

### 2. Configure Database Connection

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hotel_management
    username: root
    password: root  # Change if you set different password
```

### 3. Build Backend

```bash
# Clean build
mvn clean install

# This will:
# - Download dependencies
# - Compile source code
# - Run tests (if any)
# - Package JAR file
```

### 4. Run Backend

```bash
# Development mode with auto-reload
mvn spring-boot:run

# Or run the compiled JAR
java -jar target/hotel-management-backend-1.0.0.jar
```

**Expected output:**
```
Started HotelManagementApplication in X.XXX seconds
```

**Backend available at:** `http://localhost:8080`

### 5. Verify Backend

```bash
# Check health endpoint
curl http://localhost:8080/api/health

# Response:
# {"success":true,"message":"Service is running","data":"OK"}
```

## Part 3: Frontend Setup

### 1. Navigate to Frontend Directory

```bash
cd hotel-management-frontend
```

### 2. Install Dependencies

```bash
npm install

# This will install all packages from package.json
# Takes 2-5 minutes depending on internet speed
```

### 3. Configure API URL

Edit `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

### 4. Start Development Server

```bash
# Start on port 4200
npm start

# Or with specific port
ng serve --port 4200
```

**Expected output:**
```
✔ Compiled successfully.
Application bundle generated successfully.

** Angular Live Development Server is listening on localhost:4200, open your browser on http://localhost:4200/ **

✔ Compiled successfully.
```

**Frontend available at:** `http://localhost:4200`

## Part 4: First Login

### 1. Open Application

Navigate to: `http://localhost:4200`

### 2. Create Account

- Click "Register here" link
- Fill in registration form:
  - Full Name: John Doe
  - Email: john@hotel.com
  - Phone: +971501234567
  - Password: MyPassword123!

### 3. Login

- Email: john@hotel.com
- Password: MyPassword123!

### 4. Access Dashboard

Upon successful login, you'll see the dashboard with KPI cards.

## Part 5: Docker Deployment

### Option 1: Docker Compose (Recommended)

```bash
# Navigate to project root
cd hotel-management

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Access Points

- Frontend: `http://localhost`
- API: `http://localhost:8080/api`
- MySQL: `localhost:3306`

### Option 2: Individual Docker Images

#### Build Backend Image

```bash
cd hotel-management-backend

# Build Maven project
mvn clean package

# Build Docker image
docker build -t hotel-backend:1.0 .

# Run container
docker run -d \
  --name hotel_backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/hotel_management \
  hotel-backend:1.0
```

#### Build Frontend Image

```bash
cd hotel-management-frontend

# Build Docker image
docker build -t hotel-frontend:1.0 .

# Run container
docker run -d \
  --name hotel_frontend \
  -p 80:80 \
  hotel-frontend:1.0
```

## Troubleshooting

### Issue: "Connection refused" on Backend

**Solution:**
```bash
# Verify MySQL is running
mysql -u root -p -e "SELECT 1;"

# If not running, start MySQL
sudo systemctl start mysql  # Linux
brew services start mysql   # Mac
# Windows: Start MySQL from Services
```

### Issue: "Port 8080 in use"

```bash
# Find process using port 8080
lsof -i :8080  # Mac/Linux
netstat -ano | findstr :8080  # Windows

# Kill process
kill -9 <PID>  # Mac/Linux
taskkill /PID <PID> /F  # Windows
```

### Issue: "Port 4200 in use"

```bash
ng serve --port 4300  # Use different port
```

### Issue: "npm install fails"

```bash
# Clear cache
npm cache clean --force

# Delete node_modules
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

### Issue: "CORS errors"

**Solution:** Ensure backend CORS config in `CorsConfig.java`:
```java
.allowedOrigins("http://localhost:4200", "http://localhost:3000")
```

## Development Workflow

### Backend Development

```bash
# 1. Make code changes
# 2. Maven auto-recompiles with devtools
# 3. Browser auto-refreshes

# Or manually rebuild
mvn clean compile
```

### Frontend Development

```bash
# 1. Make code changes
# 2. Angular CLI watches files
# 3. Browser auto-refreshes (hot reload)

# Lint code
ng lint

# Run tests
ng test
```

### Database Changes

```bash
# Add new migration file
touch src/main/resources/db/migration/V2__add_new_table.sql

# Flyway automatically runs on next startup
mvn spring-boot:run
```

## Production Deployment

### Before Going Live

1. **Update JWT Secret**
   ```yaml
   jwt:
     secret: your_production_secret_key_here
   ```

2. **Configure Database**
   - Use production MySQL server
   - Set up backups
   - Configure connection pooling

3. **Build Production Bundle**
   ```bash
   # Backend
   mvn clean package -Pprod
   
   # Frontend
   npm run build
   ```

4. **Set Environment Variables**
   ```bash
   export SPRING_DATASOURCE_USERNAME=prod_user
   export SPRING_DATASOURCE_PASSWORD=prod_password
   export GEMINI_API_KEY=your_gemini_key
   ```

5. **Deploy with Docker**
   ```bash
   docker-compose -f docker-compose.prod.yml up -d
   ```

## Useful Commands

### Backend

```bash
# Build only (no tests)
mvn clean package -DskipTests

# Run tests
mvn test

# Generate JAR
mvn clean package

# Check dependencies
mvn dependency:tree
```

### Frontend

```bash
# Build for production
npm run build

# Run tests with coverage
npm run test -- --code-coverage

# Lint and fix code
ng lint --fix

# Generate component
ng generate component component-name
```

### Docker

```bash
# View images
docker images

# View running containers
docker ps

# View container logs
docker logs -f container_name

# Enter container shell
docker exec -it container_name bash

# Remove container
docker rm container_name

# Remove image
docker rmi image_name
```

## Verification Checklist

After setup, verify:

- [ ] Backend running on `http://localhost:8080`
- [ ] Backend health check returns success
- [ ] Frontend running on `http://localhost:4200`
- [ ] Can register new account
- [ ] Can login with credentials
- [ ] Dashboard displays correctly
- [ ] Can navigate between pages
- [ ] No console errors in browser
- [ ] No errors in backend logs

## Next Steps

1. Review [README.md](README.md) for full documentation
2. Check [QUICKSTART.md](QUICKSTART.md) for quick reference
3. Explore database schema in `src/main/resources/db/migration`
4. Review API endpoints in backend controllers
5. Customize UI in frontend components
6. Add your business logic

## Support

For detailed help:
1. Check application logs
2. Review error messages carefully
3. Search error in Google
4. Check Spring Boot and Angular documentation
5. Ask in community forums

---

**You're all set! Happy coding! 🎉**
