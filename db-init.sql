CREATE TABLE IF NOT EXISTS roles (
                                     role_id SERIAL PRIMARY KEY,
                                     role_type VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO roles (role_id, role_type) VALUES
                                           (1, 'ADMIN'),
                                           (2, 'BASIC')
ON CONFLICT (role_id, role_type) DO NOTHING;