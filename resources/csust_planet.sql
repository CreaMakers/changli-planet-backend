/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : localhost:3306
 Source Schema         : csust_planet

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 15/10/2024 21:10:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_group
-- ----------------------------
DROP TABLE IF EXISTS `chat_group`;
CREATE TABLE `chat_group`  (
  `group_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '群聊ID',
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '群聊名称',
  `member_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '当前群聊人数',
  `member_limit` int UNSIGNED NOT NULL DEFAULT 100 COMMENT '群聊人数限制',
  `type` tinyint NOT NULL COMMENT '群聊类型: 1-学习, 2-生活, 3-工具, 4-问题反馈, 5-社团, 6-比赛',
  `requires_approval` tinyint(1) NULL DEFAULT 0 COMMENT '是否需要审核: 0-否, 1-是',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `is_banned` tinyint NULL DEFAULT NULL COMMENT '是否封禁: 0-未封禁，1-已封禁',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群聊头像URL',
  `background_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群聊背景图片URL',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '群聊更新时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '群聊创建时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群聊描述',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '群聊表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_announcement
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_announcement`;
CREATE TABLE `chat_group_announcement`  (
  `announcement_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `group_id` int UNSIGNED NOT NULL COMMENT '所属群聊ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '发布用户ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `is_pinned` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶公告: 1-置顶, 0-不置顶',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '公告创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '公告更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '公告描述',
  PRIMARY KEY (`announcement_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_apply
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_apply`;
CREATE TABLE `chat_group_apply`  (
  `apply_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `group_id` int UNSIGNED NOT NULL COMMENT '群聊ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '申请用户ID',
  `apply_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '申请理由或附加信息',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '申请状态: 0-待审核, 1-已批准, 2-已拒绝',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `processed_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `processed_by` int UNSIGNED NULL DEFAULT NULL COMMENT '处理申请的管理员ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户申请动态数据描述',
  PRIMARY KEY (`apply_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户申请加入群聊表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_group_file
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_file`;
CREATE TABLE `chat_group_file`  (
  `file_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `group_id` int UNSIGNED NOT NULL COMMENT '所属群聊ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '上传用户ID',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名称',
  `file_size` bigint UNSIGNED NOT NULL COMMENT '文件大小（字节）',
  `file_type` tinyint UNSIGNED NOT NULL COMMENT '文件类型: 1-图片, 2-文档, 3-视频, 4-音频, 5-其他',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件存储的URL路径',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '文件上传时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '文件更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件描述',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件系统表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_message`;
CREATE TABLE `chat_group_message`  (
  `message_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `group_id` int NOT NULL COMMENT '所属群聊ID',
  `sender_id` int UNSIGNED NOT NULL COMMENT '发送者用户ID',
  `receiver_id` int UNSIGNED NULL DEFAULT NULL COMMENT '回复目标的用户ID（回复某人的消息）',
  `message_content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '聊天内容',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件存储的URL路径',
  `file_type` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '文件类型: 1-图片, 2-文档, 3-视频, 4-音频, 5-其他',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息发送时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '消息更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息描述',
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `idx_group_id`(`group_id` ASC) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '群聊消息记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_message_read_info
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_message_read_info`;
CREATE TABLE `chat_group_message_read_info`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `group_id` int UNSIGNED NOT NULL COMMENT '群聊ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `message_id` int UNSIGNED NOT NULL COMMENT '消息ID',
  `is_read` tinyint(1) NOT NULL DEFAULT 0 COMMENT '消息是否已读: 0-未读, 1-已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_group_user_message`(`group_id` ASC, `user_id` ASC, `message_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户未读消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_group_post
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_post`;
CREATE TABLE `chat_group_post`  (
  `post_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `group_id` int UNSIGNED NOT NULL COMMENT '所属群聊ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '发布用户ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子内容',
  `category` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '帖子类别: 0-general, 1-tutorial, 2-article, 3-experience',
  `is_pinned` tinyint(1) NULL DEFAULT 0 COMMENT '是否加精: 0-否, 1-是',
  `view_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览人数',
  `coin_count` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '被投币量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '帖子创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '帖子更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '帖子描述',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识区帖子表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_user
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_user`;
CREATE TABLE `chat_group_user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `group_id` int UNSIGNED NOT NULL COMMENT '群聊ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `joined_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户加入群聊时间',
  `role` tinyint(1) NOT NULL DEFAULT 0 COMMENT '用户角色: 0-普通成员, 1-管理员, 2-群主',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `is_muted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否在该群聊被禁言: 0-未禁言, 1-已禁言',
  `mute_start_time` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '禁言开始时间',
  `mute_duration` int NOT NULL DEFAULT 0 COMMENT '禁言持续时间（分钟）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `join_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '用户入群状态: 0-审核中, 1-已入群, 2-审核拒绝',
  `join_request_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户入群申请信息',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户-群聊关联表描述',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_group`(`group_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-群聊关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for comment_like
-- ----------------------------
DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like`  (
  `like_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `comment_id` int UNSIGNED NOT NULL COMMENT '所属评论ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '点赞更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '点赞描述',
  PRIMARY KEY (`like_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `notification_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `sender_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '发送者ID, 如果是系统通知可以为空',
  `receiver_id` bigint UNSIGNED NOT NULL COMMENT '接收者ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `send_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知描述',
  PRIMARY KEY (`notification_id`) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_coin
-- ----------------------------
DROP TABLE IF EXISTS `post_coin`;
CREATE TABLE `post_coin`  (
  `coin_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '投币ID',
  `post_id` int UNSIGNED NOT NULL COMMENT '所属帖子ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '投币用户ID',
  `coin_amount` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '投币数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投币时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '投币更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '投币描述',
  PRIMARY KEY (`coin_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子投币表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_comment
-- ----------------------------
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment`  (
  `comment_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` int UNSIGNED NOT NULL COMMENT '所属帖子ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '评论用户ID',
  `parent_comment_id` int UNSIGNED NULL DEFAULT NULL COMMENT '父评论ID，表示是否回复别人的评论',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '评论更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论描述',
  PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_like
-- ----------------------------
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like`  (
  `like_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `post_id` int UNSIGNED NOT NULL COMMENT '所属帖子ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '点赞更新时间',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '点赞描述',
  PRIMARY KEY (`like_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for report_post
-- ----------------------------
DROP TABLE IF EXISTS `report_post`;
CREATE TABLE `report_post`  (
  `report_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `post_id` bigint UNSIGNED NOT NULL COMMENT '被举报的帖子ID',
  `reporter_id` bigint UNSIGNED NOT NULL COMMENT '举报者的用户ID',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报原因',
  `report_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '处理状态: 0-未处理, 1-已处理',
  `process_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理描述',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '帖子举报表描述',
  PRIMARY KEY (`report_id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_reporter_id`(`reporter_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子举报表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for report_user
-- ----------------------------
DROP TABLE IF EXISTS `report_user`;
CREATE TABLE `report_user`  (
  `report_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `reported_user_id` bigint UNSIGNED NOT NULL COMMENT '被举报的用户ID',
  `reporter_id` bigint UNSIGNED NOT NULL COMMENT '举报者的用户ID',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报原因',
  `report_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '处理状态: 0-未处理, 1-已处理',
  `process_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理描述',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户举报表描述',
  PRIMARY KEY (`report_id`) USING BTREE,
  INDEX `idx_reported_user_id`(`reported_user_id` ASC) USING BTREE,
  INDEX `idx_reporter_id`(`reporter_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_is_deleted`(`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户举报表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码，经过加密存储',
  `is_admin` tinyint NULL DEFAULT 0 COMMENT '管理员权限: 0-普通用户, 1-运营组，2-开发组',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
  `is_banned` tinyint NULL DEFAULT 0 COMMENT '是否封禁: 0-未封禁，1-已封禁',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户描述',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `idx_username_is_deleted`(`username` ASC, `is_deleted` ASC) USING BTREE,
  INDEX `idx_is_banned_is_admin`(`is_banned` ASC, `is_admin` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户基础信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_feedback
-- ----------------------------
DROP TABLE IF EXISTS `user_feedback`;
CREATE TABLE `user_feedback`  (
  `feedback_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `user_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '用户ID, 如果是匿名反馈可以为空',
  `feedback_type` tinyint NOT NULL COMMENT '反馈类型: 1-Bug反馈, 2-改进建议',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反馈内容',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '处理状态: 0-未处理, 1-处理中, 2-已解决, 3-已关闭',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户反馈表描述',
  PRIMARY KEY (`feedback_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户反馈表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_group_last_read
-- ----------------------------
DROP TABLE IF EXISTS `user_group_last_read`;
CREATE TABLE `user_group_last_read`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `group_id` int NOT NULL COMMENT '群聊ID',
  `last_read_message_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户最后一次浏览的消息ID',
  `last_read_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后浏览时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user_group`(`user_id` ASC, `group_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户群聊浏览状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_profile
-- ----------------------------
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile`  (
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像URL',
  `bio` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个性标签/个人描述',
  `user_level` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '用户等级',
  `gender` tinyint NULL DEFAULT 2 COMMENT '性别: 0-男, 1-女, 2-其他',
  `grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '年级',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所在地',
  `website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个人网站或社交链接',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户展示信息描述',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户展示信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_stats
-- ----------------------------
DROP TABLE IF EXISTS `user_stats`;
CREATE TABLE `user_stats`  (
  `user_id` int UNSIGNED NOT NULL COMMENT '用户ID',
  `student_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号，唯一',
  `article_count` int NULL DEFAULT 0 COMMENT '发表文章数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论次数',
  `statement_count` int NULL DEFAULT 0 COMMENT '发言次数',
  `liked_count` int NULL DEFAULT 0 COMMENT '收到点赞次数',
  `coin_count` int NULL DEFAULT 0 COMMENT '硬币数量',
  `xp` int NULL DEFAULT 0 COMMENT '经验值',
  `quiz_type` tinyint NULL DEFAULT 0 COMMENT '考核通过状态',
  `last_login_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户动态数据描述',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户动态数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for violation_record
-- ----------------------------
DROP TABLE IF EXISTS `violation_record`;
CREATE TABLE `violation_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int UNSIGNED NOT NULL COMMENT '违规用户ID',
  `violation_type` tinyint NOT NULL COMMENT '违规类型: 1-言论违规，2-行为违规，3-其他',
  `penalty_type` tinyint NOT NULL DEFAULT 0 COMMENT '处罚类型: 0-无，1-警告，2-封禁，3-禁言',
  `penalty_status` tinyint NOT NULL DEFAULT 0 COMMENT '处罚状态: 0-未处罚, 1-处罚中, 2-处罚完成',
  `violation_time` datetime NOT NULL COMMENT '违规时间',
  `penalty_time` datetime NULL DEFAULT NULL COMMENT '处罚时间',
  `mute_duration` int NULL DEFAULT NULL COMMENT '禁言持续时间（分钟）',
  `ban_duration` int NULL DEFAULT NULL COMMENT '封禁持续时间（分钟）',
  `penalty_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处罚原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '违规行为描述',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_violation_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户违规记录表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
