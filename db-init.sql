CREATE TABLE IF NOT EXISTS roles (
                                     id   SERIAL PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (name) VALUES
                             ('ADMIN'),
                             ('BASIC')
ON CONFLICT (name) DO NOTHING;
