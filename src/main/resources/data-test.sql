INSERT INTO roles (role_id, role_type) VALUES
    (1, 'ADMIN'),
    (2, 'BASIC');

INSERT INTO budgets (budget_id, period, total_income_planned, total_outcome_planned) VALUES
    (1, '2025-Q1', 10000.00, 5000.00),
    (2, '2025-Q2', 8000.00, 3000.00),
    (3, '2025-Q3', 12000.00, 7000.00),
    (4, '2025-Q4', 9000.00, 4000.00),
    (5, '2026-Q1', 11000.00, 6000.00);

INSERT INTO categories (category_id, name, description) VALUES
    (1, 'Alimentos', 'Gastos en comida y víveres'),
    (2, 'Transporte', 'Movilidad diaria'),
    (3, 'Servicios', 'Luz, agua, internet'),
    (4, 'Educación', 'Pagos académicos'),
    (5, 'Ocio', 'Entretenimiento y recreación');

INSERT INTO goal_contributions (contribution_id, amount, date) VALUES
    (1, 200.00, '2025-01-10'),
    (2, 150.00, '2025-02-15'),
    (3, 300.00, '2025-03-20'),
    (4, 400.00, '2025-04-25'),
    (5, 100.00, '2025-05-01');

INSERT INTO saving_goals (goal_id, contribution_id, name, target_amount, current_amount, due_date) VALUES
    (1, 1, 'Viaje a Cusco', 1000.00, 200.00, '2025-08-01'),
    (2, 2, 'Laptop nueva', 2000.00, 150.00, '2025-09-01'),
    (3, 3, 'Curso online', 500.00, 300.00, '2025-06-15'),
    (4, 4, 'Bicicleta', 800.00, 400.00, '2025-07-10'),
    (5, 5, 'Monitor', 600.00, 100.00, '2025-10-01');

INSERT INTO spending_limits (limit_id, category_id, monthly_limit) VALUES
    (1, 1, 500.00),
    (2, 2, 300.00),
    (3, 3, 400.00),
    (4, 4, 600.00),
    (5, 5, 200.00);

INSERT INTO financial_movements (movement_id, movement_type, category_id, amount, date, currency) VALUES
    (1, 'OUTCOME', 3, 250.00, '2025-05-15', 'PEN'),
    (2, 'INCOME', 1, 1000.00, '2025-05-01', 'USD'),
    (3, 'OUTCOME', 2, 120.00, '2025-05-10', 'EUR'),
    (4, 'OUTCOME', 5, 75.00, '2025-05-12', 'PEN'),
    (5, 'INCOME', 4, 500.00, '2025-05-08', 'USD');

INSERT INTO users (user_id, budget_id, role_id, goal_id, limit_id, category_id, movement_id, email, password, first_name, last_name, user_type) VALUES
    (1, 1, 1, 1, 1, 1, 1, 'admin@financello.com', 'hashed_pwd_1', 'Admin', 'User', 'PERSONAL'),
    (2, 2, 2, 2, 2, 2, 2, 'basic1@financello.com', 'hashed_pwd_2', 'Ana', 'Torres', 'BUSINESS'),
    (3, 3, 2, 3, 3, 3, 3, 'basic2@financello.com', 'hashed_pwd_3', 'Luis', 'Martínez', 'PERSONAL'),
    (4, 4, 2, 4, 4, 4, 4, 'basic3@financello.com', 'hashed_pwd_4', 'Sofía', 'López', 'BUSINESS'),
    (5, 5, 2, 5, 5, 5, 5, 'basic4@financello.com', 'hashed_pwd_5', 'Carlos', 'Reyes', 'PERSONAL');
