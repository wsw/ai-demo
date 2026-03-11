-- V2: 为用户查询添加额外索引
-- 注意：username 和 email 已有 UNIQUE 约束，会自动创建索引
-- 此迁移主要添加复合索引以优化常用查询

-- 复合索引：status + created_at (用于按状态分页查询)
CREATE INDEX IF NOT EXISTS idx_status_created_at ON users(status, created_at);

-- 备注:
-- 1. username 和 email 列已有 UNIQUE 约束，不需要额外索引
-- 2. idx_status 和 idx_created_at 已在 V1 中创建
-- 3. 复合索引 idx_status_created_at 优化 getUsersByStatus 查询
--
-- 回滚脚本 (仅供参考，Flyway 不支持自动回滚):
-- DROP INDEX IF EXISTS idx_status_created_at ON users;
