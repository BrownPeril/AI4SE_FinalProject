-- Sample milk tea records for demo
INSERT INTO milk_tea_records (brand, drink_name, price, consume_date, sugar_level, ice_level, cup_size, tea_base, rating, comment, would_recommend, created_at, updated_at) VALUES
('喜茶', '多肉葡萄', 25.00, '2026-06-01 14:30:00', '半糖', '少冰', '大杯', '绿茶', 4, '清爽好喝，果肉很多', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('茶百道', '豆乳玉麒麟', 18.00, '2026-06-02 10:15:00', '微糖', '去冰', '中杯', '乌龙茶', 3, '豆乳味浓郁', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('蜜雪冰城', '冰鲜柠檬水', 6.00, '2026-06-03 16:00:00', '全糖', '多冰', '大杯', NULL, 3, '便宜解渴', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('喜茶', '满杯红柚', 22.00, '2026-06-04 11:00:00', '少糖', '少冰', '大杯', '绿茶', 5, '柚子味道很正', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('奈雪的茶', '霸气草莓', 28.00, '2026-06-05 15:20:00', '半糖', '正常冰', '大杯', '绿茶', 4, '草莓很新鲜', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('CoCo', '奶茶三兄弟', 15.00, '2026-06-06 09:45:00', '全糖', '正常冰', '中杯', '红茶', 3, '经典款，稳定发挥', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('一点点', '波霸奶茶', 16.00, '2026-06-07 13:30:00', '半糖', '少冰', '大杯', '红茶', 4, '波霸Q弹', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('喜茶', '烤黑糖波波牛乳', 26.00, '2026-06-08 17:00:00', '少糖', '去冰', '大杯', NULL, 5, '黑糖味超浓，冬天必点', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('茶百道', '杨枝甘露', 19.00, '2026-06-09 14:10:00', '半糖', '少冰', '中杯', NULL, 4, '芒果味正宗', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('蜜雪冰城', '摩天脆脆', 8.00, '2026-06-10 10:00:00', NULL, NULL, NULL, NULL, 2, '冰淇淋一般般', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);