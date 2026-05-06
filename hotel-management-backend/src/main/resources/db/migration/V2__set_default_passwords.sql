-- V2__set_default_passwords.sql
-- Replace the placeholder password hashes from V1 seed data with real BCrypt hashes
-- so the default admin and receptionist accounts can log in.
--
-- Default credentials provisioned by this migration:
--   admin@hotel.com         / Admin@123
--   receptionist@hotel.com  / Recept@123

UPDATE users
SET password = '$2b$12$s7TuIZ9Ts1tj2s5mwcOxfOzEY2rFfxH9e4J.7O1BegqGrW8XXG3kK',
    updated_at = UNIX_TIMESTAMP() * 1000
WHERE email = 'admin@hotel.com';

UPDATE users
SET password = '$2b$12$8O1ZVM06suG0JLYi9v9FVOSzlijKITJJYXvKHq0vszmNjNgeGl4F6',
    updated_at = UNIX_TIMESTAMP() * 1000
WHERE email = 'receptionist@hotel.com';
