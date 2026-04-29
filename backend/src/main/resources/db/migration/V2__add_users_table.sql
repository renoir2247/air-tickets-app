CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_users_role CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
);

CREATE INDEX idx_users_username ON users(username);

-- Seed data: cashier (ROLE_USER) and admin (ROLE_ADMIN)
-- Passwords: cashier / admin (BCrypt encoded)
INSERT INTO users (username, password, role) VALUES
    ('cashier', '$2a$10$OTfMVyqNvOeB6fCS7OX5PujI2ScNu2Ntb0bgwENFOmYOylbFAGzSi', 'ROLE_USER'),
    ('admin', '$2a$10$FErJgzmFWVde75z3EdJjTewVNrPmSoaCd2PH2pi3ydSlct7YlxqWS', 'ROLE_ADMIN');
