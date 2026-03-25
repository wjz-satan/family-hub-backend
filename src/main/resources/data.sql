INSERT INTO users (id, phone, password, openid, nickname, avatar_url, enabled, created_at, updated_at)
VALUES
    (1, '13800000001', '$2y$10$gY6L1H6EQnU6hVe0NHkRnOM0jtVoDpYrzbUz9CFaVHkRAv/U55/3a', NULL, '妈妈', 'https://example.com/avatar/mom.png', TRUE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (2, '13800000002', '$2y$10$gY6L1H6EQnU6hVe0NHkRnOM0jtVoDpYrzbUz9CFaVHkRAv/U55/3a', NULL, '爸爸', 'https://example.com/avatar/dad.png', TRUE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (3, '13800000003', '$2y$10$gY6L1H6EQnU6hVe0NHkRnOM0jtVoDpYrzbUz9CFaVHkRAv/U55/3a', NULL, '孩子', 'https://example.com/avatar/kid.png', TRUE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO families (id, name, avatar_url, invite_code, announcement, owner_id, active, created_at, updated_at)
VALUES
    (1, '幸福一家', 'https://example.com/family/home.png', 'HOME88', '周末一起整理房间', 1, TRUE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO family_members (id, family_id, user_id, role, alias, joined_at, active)
VALUES
    (1, 1, 1, 'SUPER_ADMIN', '妈妈', CURRENT_TIMESTAMP(), TRUE),
    (2, 1, 2, 'ADMIN', '爸爸', CURRENT_TIMESTAMP(), TRUE),
    (3, 1, 3, 'MEMBER', '儿子', CURRENT_TIMESTAMP(), TRUE);

INSERT INTO tasks (id, family_id, title, description, priority, status, creator_id, due_date, repeat_rule, reward_points, completed_at, created_at, updated_at)
VALUES
    (1, 1, '打扫客厅', '周末大扫除', 'HIGH', 'IN_PROGRESS', 1, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 4 HOUR), 'WEEKLY', 10, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (2, 1, '倒垃圾', '晚饭后处理', 'MEDIUM', 'DONE', 2, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 2 HOUR), NULL, 5, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO task_assignees (task_id, user_id) VALUES (1, 2), (1, 3), (2, 3);
INSERT INTO task_tags (task_id, tag_value) VALUES (1, '清洁'), (2, '日常');

INSERT INTO calendar_events (id, family_id, title, description, location, start_time, end_time, all_day, color, creator_id, remind_before_minutes, repeat_rule, created_at, updated_at)
VALUES
    (1, 1, '接孩子放学', '校门口见', '学校', DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 HOUR), DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 4 HOUR), FALSE, '#FF7043', 1, 15, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO calendar_event_participants (event_id, user_id) VALUES (1, 1), (1, 3);

INSERT INTO bills (id, family_id, member_id, type, amount, category, payment_method, bill_date, note, created_at, updated_at)
VALUES
    (1, 1, 1, 'EXPENSE', 128.50, '餐饮', 'WECHAT', CURRENT_DATE(), '超市采购', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (2, 1, 2, 'INCOME', 8000.00, '工资', 'CARD', CURRENT_DATE(), '月度工资', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
