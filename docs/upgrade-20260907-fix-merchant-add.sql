-- 修复商户资料新增时 login_name 为空导致的数据库约束异常。
-- 登录账号由独立账号配置流程管理，商户资料可以先创建。
ALTER TABLE `erp_merchant`
    MODIFY COLUMN `login_name` varchar(30)
        CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
        DEFAULT NULL COMMENT '商户登录账号';
