-- V1__initial_schema.sql
-- Hotel Management System Database Schema

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL DEFAULT 'GUEST',
    id_proof_type VARCHAR(50),
    id_proof_number VARCHAR(100),
    preferred_currency VARCHAR(10) DEFAULT 'AED',
    is_active BOOLEAN DEFAULT TRUE,
    first_login_password_change_required BOOLEAN DEFAULT FALSE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    KEY idx_email (email),
    KEY idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create room_types table
CREATE TABLE IF NOT EXISTS room_types (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    base_price DECIMAL(10, 2) NOT NULL,
    max_occupancy INT NOT NULL,
    bed_type VARCHAR(50) NOT NULL,
    amenities JSON,
    photo_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    KEY idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create rooms table
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(50) UNIQUE NOT NULL,
    floor INT NOT NULL,
    room_type_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    is_active BOOLEAN DEFAULT TRUE,
    last_cleaned BIGINT,
    notes TEXT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    FOREIGN KEY (room_type_id) REFERENCES room_types(id),
    KEY idx_room_number (room_number),
    KEY idx_room_type_status (room_type_id, status),
    KEY idx_room_floor (floor),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    guest_id BIGINT NOT NULL,
    room_id BIGINT,
    room_type_id BIGINT NOT NULL,
    check_in_date BIGINT NOT NULL,
    check_out_date BIGINT NOT NULL,
    num_adults INT NOT NULL,
    num_children INT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    source VARCHAR(50) NOT NULL DEFAULT 'PORTAL',
    special_requests TEXT,
    total_amount DECIMAL(12, 2) NOT NULL,
    early_check_in_requested BOOLEAN DEFAULT FALSE,
    late_check_out_requested BOOLEAN DEFAULT FALSE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    FOREIGN KEY (guest_id) REFERENCES users(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (room_type_id) REFERENCES room_types(id),
    KEY idx_guest_id (guest_id),
    KEY idx_room_id (room_id),
    KEY idx_check_in_date (check_in_date),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create invoices table
CREATE TABLE IF NOT EXISTS invoices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT UNIQUE NOT NULL,
    room_charges DECIMAL(12, 2) NOT NULL,
    extra_charges DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(12, 2) NOT NULL,
    tax_amount DECIMAL(12, 2) NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    paid_amount DECIMAL(12, 2) NOT NULL,
    balance_due DECIMAL(12, 2) NOT NULL,
    payment_mode VARCHAR(50),
    payment_status VARCHAR(50) NOT NULL DEFAULT 'UNPAID',
    notes TEXT,
    generated_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    KEY idx_reservation_id (reservation_id),
    KEY idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create room_extras table
CREATE TABLE IF NOT EXISTS room_extras (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    extra_type VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    amount DECIMAL(10, 2) NOT NULL,
    charge_date BIGINT NOT NULL,
    added_by_id BIGINT,
    created_at BIGINT NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    FOREIGN KEY (added_by_id) REFERENCES users(id),
    KEY idx_reservation_id (reservation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create housekeeping_tasks table
CREATE TABLE IF NOT EXISTS housekeeping_tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    assigned_to_id BIGINT NOT NULL,
    task_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    scheduled_date BIGINT NOT NULL,
    completed_at BIGINT,
    notes TEXT,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (assigned_to_id) REFERENCES users(id),
    KEY idx_room_id (room_id),
    KEY idx_assigned_to (assigned_to_id),
    KEY idx_status_date (status, scheduled_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create maintenance_requests table
CREATE TABLE IF NOT EXISTS maintenance_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    reported_by_id BIGINT NOT NULL,
    issue_description TEXT NOT NULL,
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    created_at BIGINT NOT NULL,
    resolved_at BIGINT,
    updated_at BIGINT NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(id),
    FOREIGN KEY (reported_by_id) REFERENCES users(id),
    KEY idx_room_id (room_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create audit_logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    action VARCHAR(255) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    details JSON,
    timestamp BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    KEY idx_user_id (user_id),
    KEY idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO users (email, name, password, phone, role, is_active, created_at, updated_at)
VALUES 
    ('admin@hotel.com', 'Admin User', '$2a$12$...', '+971501234567', 'ADMIN', TRUE, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
    ('receptionist@hotel.com', 'Receptionist', '$2a$12$...', '+971502345678', 'RECEPTIONIST', TRUE, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);

INSERT INTO room_types (type_name, description, base_price, max_occupancy, bed_type, is_active, created_at, updated_at)
VALUES 
    ('Single Room', 'Comfortable room for one guest', 500.00, 1, 'SINGLE', TRUE, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
    ('Double Room', 'Spacious room for couples', 750.00, 2, 'DOUBLE', TRUE, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
    ('Suite', 'Luxury suite with separate living area', 1500.00, 4, 'KING', TRUE, UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
