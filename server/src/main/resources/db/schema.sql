-- =========================================================
-- 租房招聘信息小程序 - 数据库初始化脚本
-- 适用：MySQL 5.7 / 8.0，字符集 utf8mb4
-- 执行：mysql -u root -p rent_job < schema.sql
-- =========================================================

CREATE DATABASE IF NOT EXISTS `rent_job`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `rent_job`;

-- ----------------------------
-- 房源表
-- ----------------------------
DROP TABLE IF EXISTS `house`;
CREATE TABLE `house` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`        VARCHAR(120) NOT NULL                COMMENT '房源标题',
  `cover`        VARCHAR(255)          DEFAULT ''     COMMENT '封面图URL',
  `images`       TEXT                                 COMMENT '图片URL数组JSON',
  `price`        DECIMAL(10,2)         DEFAULT 0      COMMENT '月租价格（元）',
  `deposit_type` VARCHAR(20)           DEFAULT ''     COMMENT '押金方式：押一付一/押一付三/面议',
  `layout`       VARCHAR(30)           DEFAULT ''     COMMENT '户型：1室1厅',
  `area`         DECIMAL(8,2)          DEFAULT 0      COMMENT '面积（平方米）',
  `floor`        VARCHAR(20)           DEFAULT ''     COMMENT '所在楼层',
  `total_floor`  VARCHAR(20)           DEFAULT ''     COMMENT '总楼层',
  `orientation`  VARCHAR(20)           DEFAULT ''     COMMENT '朝向',
  `decoration`   VARCHAR(20)           DEFAULT ''     COMMENT '装修情况',
  `city`         VARCHAR(40)           DEFAULT ''     COMMENT '城市',
  `district`     VARCHAR(40)           DEFAULT ''     COMMENT '区域/商圈',
  `address`      VARCHAR(255)          DEFAULT ''     COMMENT '详细地址',
  `tags`         VARCHAR(255)          DEFAULT ''     COMMENT '标签，逗号分隔',
  `facilities`   VARCHAR(500)          DEFAULT ''     COMMENT '配套设施，逗号分隔',
  `description`  TEXT                                 COMMENT '房源描述',
  `contact_name` VARCHAR(40)           DEFAULT ''     COMMENT '联系人',
  `contact_phone` VARCHAR(30)          DEFAULT ''     COMMENT '联系电话',
  `contact_wechat` VARCHAR(60)         DEFAULT ''     COMMENT '微信号',
  `recommend`    TINYINT              DEFAULT 0      COMMENT '是否推荐：0否 1是',
  `status`       TINYINT              DEFAULT 1      COMMENT '状态：0下架 1上架',
  `sort`         INT                  DEFAULT 0      COMMENT '排序值，越大越靠前',
  `view_count`   INT                  DEFAULT 0      COMMENT '浏览量',
  `deleted`      TINYINT              DEFAULT 0      COMMENT '逻辑删除：0未删 1已删',
  `create_time`  DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_house_status_sort` (`status`, `sort`, `id`),
  KEY `idx_house_district` (`district`),
  KEY `idx_house_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源信息表';

-- ----------------------------
-- 招聘表
-- ----------------------------
DROP TABLE IF EXISTS `job`;
CREATE TABLE `job` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`         VARCHAR(120) NOT NULL                COMMENT '职位名称',
  `company_name`  VARCHAR(120)          DEFAULT ''     COMMENT '公司名称',
  `company_logo`  VARCHAR(255)          DEFAULT ''     COMMENT '公司LOGO',
  `company_type`  VARCHAR(60)           DEFAULT ''     COMMENT '公司性质/行业',
  `job_type`      VARCHAR(20)           DEFAULT '临时'  COMMENT '工作类型：全职/兼职/实习/临时（社区场景默认临时）',
  `salary_min`    INT                   DEFAULT 0      COMMENT '薪资下限（元/月）',
  `salary_max`    INT                   DEFAULT 0      COMMENT '薪资上限（元/月）',
  `salary_text`   VARCHAR(60)           DEFAULT ''     COMMENT '薪资文案（填写后优先展示，如 150元/天）',
  `pay_type`      VARCHAR(20)           DEFAULT ''     COMMENT '结算方式：日结/周结/月结/完工结',
  `city`          VARCHAR(40)           DEFAULT ''     COMMENT '工作城市（选填，社区场景可不填）',
  `district`      VARCHAR(40)           DEFAULT ''     COMMENT '所在小区/区域（社区场景必填）',
  `address`       VARCHAR(255)          DEFAULT ''     COMMENT '工作地址',
  `work_time`     VARCHAR(60)           DEFAULT ''     COMMENT '工作时段，如 早班 9:00-14:00',
  `experience`    VARCHAR(30)           DEFAULT ''     COMMENT '经验要求（选填）',
  `education`     VARCHAR(30)           DEFAULT ''     COMMENT '学历要求（选填，社区临工场景一般不限）',
  `recruit_count` VARCHAR(30)           DEFAULT ''     COMMENT '招聘人数',
  `welfare`       VARCHAR(500)          DEFAULT ''     COMMENT '福利，逗号分隔',
  `tags`          VARCHAR(255)          DEFAULT ''     COMMENT '标签，逗号分隔',
  `description`   TEXT                                 COMMENT '职位描述',
  `contact_name`  VARCHAR(40)           DEFAULT ''     COMMENT '联系人',
  `contact_phone` VARCHAR(30)           DEFAULT ''     COMMENT '联系电话',
  `contact_wechat` VARCHAR(60)          DEFAULT ''     COMMENT '微信号',
  `recommend`     TINYINT              DEFAULT 0      COMMENT '是否推荐：0否 1是',
  `status`        TINYINT              DEFAULT 1      COMMENT '状态：0下架 1上架',
  `sort`          INT                  DEFAULT 0      COMMENT '排序值',
  `view_count`    INT                  DEFAULT 0      COMMENT '浏览量',
  `deleted`       TINYINT              DEFAULT 0      COMMENT '逻辑删除',
  `create_time`   DATETIME             DEFAULT CURRENT_TIMESTAMP,
  `update_time`   DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_job_status_sort` (`status`, `sort`, `id`),
  KEY `idx_job_district` (`district`),
  KEY `idx_job_salary` (`salary_max`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招聘信息表';

-- ----------------------------
-- 首页轮播表
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT,
  `title`      VARCHAR(120)          DEFAULT ''     COMMENT '标题',
  `image`      VARCHAR(255) NOT NULL                COMMENT '图片URL',
  `link_type`  VARCHAR(20)           DEFAULT 'none' COMMENT '跳转类型：none/house/job/url',
  `link_id`    BIGINT                DEFAULT NULL   COMMENT '关联房源/招聘ID',
  `link_url`   VARCHAR(500)          DEFAULT ''     COMMENT '外链地址（需配置业务域名）',
  `sort`       INT                   DEFAULT 0      COMMENT '排序值',
  `status`     TINYINT               DEFAULT 1      COMMENT '状态：0禁用 1启用',
  `deleted`    TINYINT               DEFAULT 0,
  `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_banner_status_sort` (`status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播表';

-- ----------------------------
-- 管理员表（首次启动会自动创建默认管理员，无需手工插入）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`          BIGINT      NOT NULL AUTO_INCREMENT,
  `username`    VARCHAR(60) NOT NULL                COMMENT '登录账号',
  `password`    VARCHAR(100) NOT NULL               COMMENT 'BCrypt 密码',
  `nickname`    VARCHAR(60)          DEFAULT ''     COMMENT '昵称',
  `role`        VARCHAR(30)          DEFAULT 'admin' COMMENT '角色：admin 管理员',
  `status`      TINYINT              DEFAULT 1      COMMENT '状态：0禁用 1启用',
  `last_login`  DATETIME             DEFAULT NULL   COMMENT '最后登录时间',
  `deleted`     TINYINT              DEFAULT 0,
  `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台管理员表';

-- ----------------------------
-- 演示数据（可选，正式上线前可删除本段）
-- ----------------------------
INSERT INTO `house` (`title`, `cover`, `images`, `price`, `deposit_type`, `layout`, `area`,
                     `floor`, `total_floor`, `orientation`, `decoration`, `city`, `district`,
                     `address`, `tags`, `facilities`, `description`, `contact_name`,
                     `contact_phone`, `recommend`, `status`, `sort`)
VALUES
('地铁口精装一居室 拎包入住', '', '[]', 1800.00, '押一付一', '1室1厅', 48.00,
 '6', '18', '朝南', '精装修', '示例市', '中心区',
 '示例路 88 号花园小区 3 栋 601', '近地铁,随时看房,新上', '床,衣柜,空调,洗衣机,冰箱,宽带,热水器',
 '房屋采光好，家电家具齐全，可拎包入住。\n步行 5 分钟到地铁 2 号线。\n周边有超市、菜市场，生活便利。',
 '王先生', '13000000000', 1, 1, 100),
('两室一厅 南北通透 近学校', '', '[]', 2600.00, '押一付三', '2室1厅', 78.00,
 '3', '11', '南北', '简装修', '示例市', '城东区',
 '示例大道 12 号阳光苑 5 栋 302', '南北通透,近学校,停车方便', '床,衣柜,空调,热水器,宽带,停车位',
 '南北通透，通风采光极佳。\n小区环境安静，物业管理规范。\n适合家庭居住。',
 '李女士', '13100000000', 1, 1, 90);

INSERT INTO `job` (`title`, `company_name`, `company_logo`, `company_type`, `job_type`,
                   `salary_min`, `salary_max`, `city`, `district`, `address`, `experience`,
                   `education`, `recruit_count`, `welfare`, `tags`, `description`,
                   `contact_name`, `contact_phone`, `recommend`, `status`, `sort`)
VALUES
('门店店长', '示例商贸有限公司', '', '零售/批发', '全职',
 6000, 9000, '示例市', '中心区', '示例路 66 号 2 楼',
 '3-5年', '大专', '2人', '五险一金,包吃住,绩效奖金,节日福利', '急招,管理岗',
 '岗位职责：\n1. 负责门店日常运营与人员管理；\n2. 完成公司下达的销售目标；\n3. 维护客户关系。\n\n任职要求：\n1. 3 年以上零售行业管理经验；\n2. 沟通能力强，有责任心。',
 '张经理', '13200000000', 1, 1, 100),
('仓库分拣员（兼职）', '示例物流有限公司', '', '交通/物流', '兼职',
 200, 260, '示例市', '城东区', '示例工业区 3 号仓',
 '不限', '不限', '10人', '日结,包工作餐,免费班车', '日结,兼职',
 '工作内容：\n1. 负责仓库货物分拣、打包；\n2. 保持库区整洁。\n\n工作时间：\n每天 8 小时，可自选班次，工资日结。',
 '陈主管', '13300000000', 1, 1, 90);
