-- 账单管理系统数据库初始化脚本

CREATE DATABASE IF NOT EXISTS bill_manager
    DEFAULT CHARSET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bill_manager;

CREATE TABLE category (
                          category_id BIGINT NOT NULL COMMENT '分类ID',
                          category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
                          category_type VARCHAR(20) NOT NULL COMMENT '类型：INCOME / EXPENSE',
                          sort INT NOT NULL DEFAULT 0 COMMENT '排序',
                          status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
                          created_time DATETIME NOT NULL ,
                          update_time DATETIME NOT NULL ,
                          PRIMARY KEY (category_id),
                          UNIQUE KEY uk_category_name_type (category_name, category_type)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bill (
                      bill_id BIGINT NOT NULL COMMENT '账单ID',
                      bill_amount DECIMAL(15, 2) NOT NULL COMMENT '金额',
                      bill_type VARCHAR(20) NOT NULL COMMENT '类型：INCOME / EXPENSE',
                      category_id BIGINT NOT NULL COMMENT '分类ID',
                      remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
                      bill_date DATE NOT NULL COMMENT '账单日期',
                      created_time DATETIME NOT NULL COMMENT '创建时间',
                      update_time DATETIME NOT NULL COMMENT '修改时间',
                      deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常 1删除',
                      PRIMARY KEY (bill_id),
                      INDEX idx_bill_date (bill_date),
                      INDEX idx_bill_type (bill_type),
                      INDEX idx_bill_category_id (category_id),
                      CONSTRAINT fk_bill_category
                          FOREIGN KEY (category_id)
                              REFERENCES category(category_id)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

INSERT INTO category (
    category_id,
    category_name,
    category_type,
    sort,
    status,
    created_time,
    update_time
) VALUES

-- =========================
-- 支出分类
-- =========================
(1001, '餐饮', 'EXPENSE', 1, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1002, '娱乐', 'EXPENSE', 2, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1003, '出行', 'EXPENSE', 3, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1004, '购物', 'EXPENSE', 4, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1005, '住房', 'EXPENSE', 5, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1006, '医疗', 'EXPENSE', 6, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1007, '教育', 'EXPENSE', 7, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1008, '通讯', 'EXPENSE', 8, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1009, '生活缴费', 'EXPENSE', 9, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1010, '日用品', 'EXPENSE', 10, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1011, '旅行', 'EXPENSE', 11, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1012, '宠物', 'EXPENSE', 12, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1013, '保险', 'EXPENSE', 13, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1014, '数码', 'EXPENSE', 14, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(1015, '其他支出', 'EXPENSE', 99, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),

-- =========================
-- 收入分类
-- =========================
(2001, '工资', 'INCOME', 1, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(2002, '奖金', 'INCOME', 2, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(2003, '兼职', 'INCOME', 3, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(2004, '投资收益', 'INCOME', 4, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(2005, '红包', 'INCOME', 5, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(2006, '退款', 'INCOME', 6, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00'),
(2007, '其他收入', 'INCOME', 99, 1, '2026-09-13 00:00:00', '2026-09-13 00:00:00');
