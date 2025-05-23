CREATE TABLE `user` (
                        `user_id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户唯一性id',
                        `user_name` VARCHAR(50) NOT NULL COMMENT '用户账户名',
                        `pass_word` VARCHAR(100) NOT NULL COMMENT '用户加密后密码',
                        `email` VARCHAR(100) COMMENT '用户注册邮箱',
                        `phone` VARCHAR(20) COMMENT '用户手机号码',
                        `role` TINYINT DEFAULT 0 COMMENT '用户角色 0-普通用户，1-VIP，2-管理员',
                        `status` TINYINT DEFAULT 2 COMMENT '用户账户状态 0-正常 1-封禁 2-待激活',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '账户注册时间',
                        `last_login` DATETIME COMMENT '账户最后登录时间',
                        `api_key` VARCHAR(255) COMMENT '用户midjourney密钥'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户类型实体';

CREATE TABLE image (
                      image_id VARCHAR(36) PRIMARY KEY COMMENT '图片唯一ID',
                      user_id BIGINT NOT NULL COMMENT '关联用户ID',
                      image_url VARCHAR(255) NOT NULL COMMENT '图片OSS访问URL',
                      create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                      status TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0-正常，1-已删除）',
                      FOREIGN KEY (user_id) REFERENCES user(user_id)
) COMMENT='图片存储表';
