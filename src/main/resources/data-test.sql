-- 1. Roles
INSERT INTO roles (role_id, role_type) VALUES
    (1, 'ADMIN'),
    (2, 'BASIC');

-- 2. Achievements
INSERT INTO achievement (id, name, description, icon, trigger_type, trigger_value) VALUES
    (1, 'Primer Ahorro', 'Completaste tu primer objetivo de ahorro', '🥇', 'GOAL_COMPLETED', 1),
    (2, 'Ahorrador Inicial', 'Contribuiste por primera vez', '💰', 'CONTRIBUTION_COUNT', 1),
    (3, 'Constancia 5', 'Contribuiste 5 veces', '🔁', 'CONTRIBUTION_COUNT', 5),
    (4, 'Meta Doble', 'Completaste 2 metas', '🏆', 'GOAL_COMPLETED', 2),
    (5, 'Ahorro de 100', 'Ahorraste 100 soles en total', '💸', 'TOTAL_SAVED', 100),
    (6, 'Ahorro de 500', 'Ahorraste 500 soles en total', '🤑', 'TOTAL_SAVED', 500),
    (7, 'Meta Triple', 'Completaste 3 metas', '🥉', 'GOAL_COMPLETED', 3),
    (8, 'Racha de 7 días', 'Contribuiste 7 días seguidos', '🔥', 'STREAK_DAYS', 7),
    (9, 'Super Ahorro', 'Ahorraste más de 1000 soles', '🚀', 'TOTAL_SAVED', 1000),
    (10, 'Veterano', '10 metas completadas', '🎖', 'GOAL_COMPLETED', 10);
