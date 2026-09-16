-- ============================================================
-- “芯迹”(Xinji) 电子设备零件溯源系统 — 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS xinji
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE xinji;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `username`    VARCHAR(50)  NOT NULL                 COMMENT '用户名，唯一',
    `password`    VARCHAR(100) NOT NULL                 COMMENT '密码（BCrypt加密）',
    `company_name` VARCHAR(100) DEFAULT NULL            COMMENT '企业名称',
    `role`        VARCHAR(20)  NOT NULL DEFAULT 'admin' COMMENT '角色：admin/consumer',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 零件表
-- ----------------------------
DROP TABLE IF EXISTS `part`;
CREATE TABLE `part` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `part_id`         VARCHAR(50)  NOT NULL                 COMMENT '零件编号（唯一，如 SMC-20260629-0001）',
    `part_name`       VARCHAR(100) NOT NULL                 COMMENT '零件名称',
    `part_type`       VARCHAR(50)  DEFAULT NULL             COMMENT '零件类型（如芯片、电阻）',
    `batch_number`    VARCHAR(50)  DEFAULT NULL             COMMENT '批次号',
    `manufacturer`    VARCHAR(100) DEFAULT NULL             COMMENT '生产厂商',
    `production_date` DATE         DEFAULT NULL             COMMENT '生产日期',
    `specification`   TEXT         DEFAULT NULL             COMMENT '规格描述',
    `status`          TINYINT      NOT NULL DEFAULT 1       COMMENT '状态：1-有效，0-已停用',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_part_id` (`part_id`),
    KEY `idx_part_name` (`part_name`),
    KEY `idx_batch_number` (`batch_number`),
    KEY `idx_manufacturer` (`manufacturer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='零件表';

-- ----------------------------
-- 3. 溯源记录表
-- ----------------------------
DROP TABLE IF EXISTS `trace_record`;
CREATE TABLE `trace_record` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT  COMMENT '主键',
    `part_id`     BIGINT       NOT NULL                 COMMENT '关联零件表的主键ID',
    `event_type`  VARCHAR(20)  NOT NULL                 COMMENT '环节类型：produce/pack/distribute/assemble/sale',
    `operator`    VARCHAR(100) DEFAULT NULL             COMMENT '操作方名称',
    `event_time`  DATETIME     NOT NULL                 COMMENT '事件发生时间',
    `location`    VARCHAR(200) DEFAULT NULL             COMMENT '地点',
    `description` VARCHAR(500) DEFAULT NULL             COMMENT '事件描述',
    `remark`      VARCHAR(200) DEFAULT NULL             COMMENT '备注',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_part_id` (`part_id`),
    KEY `idx_event_time` (`event_time`),
    CONSTRAINT `fk_trace_part` FOREIGN KEY (`part_id`) REFERENCES `part` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源记录表';

-- ----------------------------
-- 4. 插入默认管理员账号 admin / admin123
-- ----------------------------
INSERT INTO `user` (`username`, `password`, `company_name`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '芯迹科技有限公司', 'admin');
-- 密码: admin123 (BCrypt)
