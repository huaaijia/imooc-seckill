-- 数据库初始化脚本

-- 创建数据库
CREATE DATABASE seckill;
-- 使用数据库
use seckill;
-- 创建秒杀库存表, 如果有一个CURRENT_TIMESTAMP，其他TIMESTAMP均要有一个DEFAULT
CREATE TABLE seckill(
`seckill_id` bigint NOT NULL auto_increment comment '商品库存id',
`name` VARCHAR (120) NOT NULL comment '商品名称',
`number` INT NOT NULL comment '库存数量',
`start_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' comment '秒杀开始时间',
`end_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' comment '秒杀结束时间',
`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
PRIMARY KEY (seckill_id),
KEY idx_start_time(start_time),
KEY idx_end_time(end_time),
KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT charset=utf8 comment='秒杀库存表'

-- 初始化数据
INSERT INTO
  seckill(name, number, start_time, end_time)
VALUES
  ('1000元秒杀iphone6', 100, '2016-10-12 00:00:00', '2016-10-13 00:00:00'),
  ('500元秒杀ipad2', 200, '2016-10-12 00:00:00', '2016-10-13 00:00:00'),
  ('300元秒杀小米4', 300, '2016-10-12 00:00:00', '2016-10-13 00:00:00'),
  ('200元秒杀红米note', 400, '2016-10-12 00:00:00', '2016-10-13 00:00:00');


-- 秒杀成功明细表
-- 用户登录认证的相关信息
CREATE TABLE success_killed(
`seckill_id` bigint NOT NULL comment '秒杀商品id',
`user_phone` bigint NOT NULL comment '用户手机号',
`state` tinyint NOT NULL DEFAULT -1 comment '状态标示: -1:无效 0:成功 1:已付款',
`create_time` TIMESTAMP NOT NULL comment '创建时间',
PRIMARY KEY(seckill_id, user_phone), /*联合主键*/
KEY idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT charset=utf8 comment='秒杀成功明细表'


-- 连接数据库控制台
mysql -h 127.0.0.1 -P 8889 -u root -pqqq111;
show tables;
show create table seckill\G

--手写DDL
--记录每次上线的DDL修改
--上线v1.1
ALTER TABLE seckill
DROP index idx_create_time,
add index idx_c_s(starte_time, create_time);


