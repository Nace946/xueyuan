-- =========================================================
-- 招聘模块字段升级（社区临工场景）
-- 适用：已按 schema.sql 建过库的环境，直接执行本增量脚本即可
-- 执行：mysql -u root -p rent_job < patch_job_20261020.sql
-- 说明：MySQL 不支持 IF NOT EXISTS 语法，若提示 duplicate column 说明字段已存在，忽略即可
-- =========================================================

USE `rent_job`;

-- 结算方式：日结 / 周结 / 月结 / 完工结
ALTER TABLE `job` ADD COLUMN `pay_type` VARCHAR(20) DEFAULT '' COMMENT '结算方式：日结/周结/月结/完工结' AFTER `salary_text`;

-- 工作时段：如 早班 9:00-14:00
ALTER TABLE `job` ADD COLUMN `work_time` VARCHAR(60) DEFAULT '' COMMENT '工作时段，如 早班 9:00-14:00' AFTER `address`;

-- 社区场景：默认工作类型改为「临时」
ALTER TABLE `job` MODIFY COLUMN `job_type` VARCHAR(20) DEFAULT '临时' COMMENT '工作类型：全职/兼职/实习/临时（社区场景默认临时）';
