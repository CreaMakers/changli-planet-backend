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

 Date: 19/04/2025 16:54:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for apk_updates
-- ----------------------------
DROP TABLE IF EXISTS `apk_updates`;
CREATE TABLE `apk_updates`
(
    `id`             int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `version_code`   int UNSIGNED                                                  NOT NULL COMMENT '版本代码',
    `version_name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '版本名称',
    `download_url`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '下载链接',
    `update_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         NULL COMMENT '更新信息',
    `create_time`    datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time`    datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_version_code` (`version_code` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = 'APK更新记录表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group
-- ----------------------------
DROP TABLE IF EXISTS `chat_group`;
CREATE TABLE `chat_group`
(
    `group_id`          int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT '群聊ID',
    `group_name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '群聊名称',
    `member_count`      int UNSIGNED                                                  NOT NULL DEFAULT 0 COMMENT '当前群聊人数',
    `member_limit`      int UNSIGNED                                                  NOT NULL DEFAULT 100 COMMENT '群聊人数限制',
    `type`              tinyint                                                       NOT NULL COMMENT '群聊类型: 1-学习, 2-生活, 3-工具, 4-问题反馈, 5-社团, 6-比赛',
    `requires_approval` tinyint(1)                                                    NULL     DEFAULT 0 COMMENT '是否需要审核: 0-否, 1-是',
    `is_deleted`        tinyint(1)                                                    NULL     DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `is_banned`         tinyint                                                       NULL     DEFAULT NULL COMMENT '是否封禁: 0-未封禁，1-已封禁',
    `avatar_url`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '群聊头像URL',
    `background_url`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '群聊背景图片URL',
    `update_time`       datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '群聊更新时间',
    `create_time`       datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '群聊创建时间',
    `description`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '群聊描述',
    PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '群聊表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_announcement
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_announcement`;
CREATE TABLE `chat_group_announcement`
(
    `announcement_id` int UNSIGNED                                                   NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `group_id`        int UNSIGNED                                                   NOT NULL COMMENT '所属群聊ID',
    `user_id`         int UNSIGNED                                                   NOT NULL COMMENT '发布用户ID',
    `title`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '公告标题',
    `content`         varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
    `is_pinned`       tinyint(1)                                                     NULL DEFAULT 0 COMMENT '是否置顶公告: 1-置顶, 0-不置顶',
    `create_time`     datetime                                                       NULL DEFAULT CURRENT_TIMESTAMP COMMENT '公告创建时间',
    `update_time`     datetime                                                       NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '公告更新时间',
    `is_deleted`      tinyint(1)                                                     NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `description`     varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL DEFAULT NULL COMMENT '公告描述',
    PRIMARY KEY (`announcement_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_apply
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_apply`;
CREATE TABLE `chat_group_apply`
(
    `apply_id`       int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    `group_id`       int UNSIGNED                                                  NOT NULL COMMENT '群聊ID',
    `user_id`        int UNSIGNED                                                  NOT NULL COMMENT '申请用户ID',
    `apply_message`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '申请理由或附加信息',
    `status`         tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '申请状态: 0-待审核, 1-已批准, 2-已拒绝',
    `apply_time`     datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `processed_time` datetime                                                      NULL     DEFAULT NULL COMMENT '处理时间',
    `processed_by`   int UNSIGNED                                                  NULL     DEFAULT NULL COMMENT '处理申请的管理员ID',
    `create_time`    datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time`    datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`     tinyint                                                       NULL     DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
    `description`    varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户申请动态数据描述',
    PRIMARY KEY (`apply_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户申请加入群聊表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_file
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_file`;
CREATE TABLE `chat_group_file`
(
    `file_id`     int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `group_id`    int UNSIGNED                                                  NOT NULL COMMENT '所属群聊ID',
    `user_id`     int UNSIGNED                                                  NOT NULL COMMENT '上传用户ID',
    `file_name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名称',
    `file_size`   bigint UNSIGNED                                               NOT NULL COMMENT '文件大小（字节）',
    `file_type`   tinyint UNSIGNED                                              NOT NULL COMMENT '文件类型: 1-图片, 2-文档, 3-视频, 4-音频, 5-其他',
    `file_url`    varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件存储的URL路径',
    `create_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '文件上传时间',
    `update_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '文件更新时间',
    `is_deleted`  tinyint(1)                                                    NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件描述',
    PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件系统表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_message`;
CREATE TABLE `chat_group_message`
(
    `message_id`      int UNSIGNED                                                   NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `group_id`        int                                                            NOT NULL COMMENT '所属群聊ID',
    `sender_id`       int UNSIGNED                                                   NOT NULL COMMENT '发送者用户ID',
    `receiver_id`     int UNSIGNED                                                   NULL DEFAULT NULL COMMENT '回复目标的用户ID（回复某人的消息）',
    `message_content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '聊天内容',
    `file_url`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL DEFAULT NULL COMMENT '文件存储的URL路径',
    `file_type`       tinyint UNSIGNED                                               NULL DEFAULT NULL COMMENT '文件类型: 1-图片, 2-文档, 3-视频, 4-音频, 5-其他',
    `extra`           json                                                           NULL COMMENT '额外消息内容',
    `gap_count`       int                                                            NULL DEFAULT NULL COMMENT '消息间隔数',
    `create_time`     datetime                                                       NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息发送时间',
    `update_time`     datetime                                                       NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '消息更新时间',
    `is_deleted`      tinyint(1)                                                     NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `description`     varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL DEFAULT NULL COMMENT '消息描述',
    PRIMARY KEY (`message_id`) USING BTREE,
    INDEX `idx_group_id` (`group_id` ASC) USING BTREE,
    INDEX `idx_receiver_id` (`receiver_id` ASC) USING BTREE,
    INDEX `idx_sender_id` (`sender_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '群聊消息记录表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_message_read_info
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_message_read_info`;
CREATE TABLE `chat_group_message_read_info`
(
    `id`          int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `group_id`    int UNSIGNED NOT NULL COMMENT '群聊ID',
    `user_id`     int UNSIGNED NOT NULL COMMENT '用户ID',
    `message_id`  int UNSIGNED NOT NULL COMMENT '消息ID',
    `is_read`     tinyint(1)   NOT NULL DEFAULT 0 COMMENT '消息是否已读: 0-未读, 1-已读',
    `create_time` datetime     NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time` datetime     NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_group_user_message` (`group_id` ASC, `user_id` ASC, `message_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户未读消息表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_post
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_post`;
CREATE TABLE `chat_group_post`
(
    `post_id`     int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
    `group_id`    int UNSIGNED                                                  NOT NULL COMMENT '所属群聊ID',
    `user_id`     int UNSIGNED                                                  NOT NULL COMMENT '发布用户ID',
    `title`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子标题',
    `content`     longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     NOT NULL COMMENT '帖子内容',
    `category`    tinyint UNSIGNED                                              NOT NULL DEFAULT 0 COMMENT '帖子类别: 0-general, 1-tutorial, 2-article, 3-experience',
    `is_pinned`   tinyint(1)                                                    NULL     DEFAULT 0 COMMENT '是否加精: 0-否, 1-是',
    `view_count`  int UNSIGNED                                                  NOT NULL DEFAULT 0 COMMENT '浏览人数',
    `coin_count`  int UNSIGNED                                                  NOT NULL DEFAULT 0 COMMENT '被投币量',
    `create_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '帖子创建时间',
    `update_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '帖子更新时间',
    `is_deleted`  tinyint(1)                                                    NULL     DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '帖子描述',
    PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '知识区帖子表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_user
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_user`;
CREATE TABLE `chat_group_user`
(
    `id`                int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `group_id`          int UNSIGNED                                                  NOT NULL COMMENT '群聊ID',
    `user_id`           int UNSIGNED                                                  NOT NULL COMMENT '用户ID',
    `joined_time`       datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户加入群聊时间',
    `role`              tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '用户角色: 0-普通成员, 1-管理员, 2-群主',
    `is_deleted`        tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `is_muted`          tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '是否在该群聊被禁言: 0-未禁言, 1-已禁言',
    `mute_start_time`   datetime                                                      NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '禁言开始时间',
    `mute_duration`     int                                                           NOT NULL DEFAULT 0 COMMENT '禁言持续时间（分钟）',
    `create_time`       datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time`       datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `join_status`       tinyint(1)                                                    NOT NULL DEFAULT 1 COMMENT '用户入群状态: 0-审核中, 1-已入群, 2-审核拒绝',
    `join_request_info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户入群申请信息',
    `description`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户-群聊关联表描述',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_user_group` (`group_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户-群聊关联表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for chat_group_user_display_name
-- ----------------------------
DROP TABLE IF EXISTS `chat_group_user_display_name`;
CREATE TABLE `chat_group_user_display_name`
(
    `id`           int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `group_id`     int UNSIGNED                                                  NOT NULL COMMENT '群聊ID',
    `user_id`      int UNSIGNED                                                  NOT NULL COMMENT '用户ID',
    `display_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户在该群聊中的显示名称',
    `create_time`  datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time`  datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`   tinyint(1)                                                    NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_group_user` (`group_id` ASC, `user_id` ASC) USING BTREE COMMENT '确保每个用户在一个群聊中只有一个显示名称'
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户群聊显示名称表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for comment_like
-- ----------------------------
DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like`
(
    `like_id`     int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `comment_id`  int UNSIGNED                                                  NOT NULL COMMENT '所属评论ID',
    `user_id`     int UNSIGNED                                                  NOT NULL COMMENT '点赞用户ID',
    `create_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    `update_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '点赞更新时间',
    `is_deleted`  tinyint(1)                                                    NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '点赞描述',
    PRIMARY KEY (`like_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论点赞表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for fresh_news
-- ----------------------------
DROP TABLE IF EXISTS `fresh_news`;
CREATE TABLE `fresh_news`
(
    `fresh_news_id`   bigint UNSIGNED                                                NOT NULL AUTO_INCREMENT COMMENT '新鲜事ID',
    `user_id`         int UNSIGNED                                                   NOT NULL COMMENT '发布用户id',
    `title`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '标题',
    `content`         text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci          NOT NULL COMMENT '内容',
    `images`          varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '新鲜事的图片，最多9张，图片路径以逗号分隔',
    `tags`            varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '新鲜事的标签，多个标签以逗号分隔',
    `liked`           int UNSIGNED                                                   NULL     DEFAULT 0 COMMENT '点赞数量',
    `comments`        int UNSIGNED                                                   NULL     DEFAULT 0 COMMENT '评论数量',
    `create_time`     timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      tinyint(1)                                                     NOT NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除',
    `allow_comments`  tinyint(1)                                                     NOT NULL DEFAULT 1 COMMENT '是否允许评论，0：不允许，1：允许',
    `favorites_count` int                                                            NOT NULL DEFAULT 0 COMMENT '被收藏数',
    PRIMARY KEY (`fresh_news_id`) USING BTREE,
    INDEX `idx_is_deleted_fresh_news` (`is_deleted` ASC) USING BTREE,
    INDEX `idx_user_deleted` (`user_id` ASC, `is_deleted` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '新鲜事表：存储用户发布的各类新鲜事内容及相关信息'
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for fresh_news_comments
-- ----------------------------
DROP TABLE IF EXISTS `fresh_news_comments`;
CREATE TABLE `fresh_news_comments`
(
    `comment_id`  bigint UNSIGNED                                                NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `user_id`     int UNSIGNED                                                   NOT NULL COMMENT '评论用户id',
    `news_id`     bigint UNSIGNED                                                NOT NULL COMMENT '新鲜事ID',
    `root`        int                                                            NULL     DEFAULT 0,
    `parent_id`   bigint UNSIGNED                                                NOT NULL DEFAULT 0 COMMENT '一级评论ID，如果是子评论则为父评论ID',
    `content`     varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
    `liked`       int UNSIGNED                                                   NULL     DEFAULT 0 COMMENT '点赞数量',
    `is_deleted`  int                                                            NULL     DEFAULT 0 COMMENT '是否删除',
    `create_time` timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`comment_id`) USING BTREE,
    INDEX `news_id` (`news_id` ASC) USING BTREE,
    INDEX `user_id` (`user_id` ASC) USING BTREE,
    CONSTRAINT `fresh_news_comments_ibfk_1` FOREIGN KEY (`news_id`) REFERENCES `fresh_news` (`fresh_news_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `fresh_news_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '新鲜事评论表：存储用户对新鲜事的评论内容'
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for fresh_news_favorites
-- ----------------------------
DROP TABLE IF EXISTS `fresh_news_favorites`;
CREATE TABLE `fresh_news_favorites`
(
    `favorites_id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键：唯一标识每条收藏记录',
    `user_id`      int UNSIGNED    NOT NULL COMMENT '用户ID，关联用户表，表示收藏该新鲜事的用户',
    `news_id`      bigint UNSIGNED NOT NULL COMMENT '新鲜事ID，关联新鲜事表，表示被收藏的新鲜事',
    `create_time`  timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间，记录用户收藏该新鲜事的时间',
    `is_deleted`   tinyint(1)      NOT NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除，逻辑删除字段',
    PRIMARY KEY (`favorites_id`) USING BTREE,
    INDEX `idx_is_deleted_favorites` (`is_deleted` ASC) USING BTREE COMMENT '索引：加速基于删除状态的查询，便于执行逻辑删除操作',
    INDEX `idx_user_news_id` (`user_id` ASC, `news_id` ASC) USING BTREE COMMENT '联合索引：加速用户和新鲜事的查询，可以快速查询某个用户收藏了哪些新鲜事',
    INDEX `news_id` (`news_id` ASC) USING BTREE,
    CONSTRAINT `fresh_news_favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `fresh_news_favorites_ibfk_2` FOREIGN KEY (`news_id`) REFERENCES `fresh_news` (`fresh_news_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '收藏功能表：存储用户收藏的新鲜事信息'
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for fresh_news_likes
-- ----------------------------
DROP TABLE IF EXISTS `fresh_news_likes`;
CREATE TABLE `fresh_news_likes`
(
    `like_id`     bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `user_id`     int UNSIGNED    NOT NULL COMMENT '点赞用户id',
    `news_id`     bigint UNSIGNED NOT NULL COMMENT '新鲜事ID',
    `is_deleted`  tinyint(1)      NOT NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除',
    `create_time` timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`like_id`) USING BTREE,
    INDEX `idx_is_deleted_likes` (`is_deleted` ASC) USING BTREE,
    INDEX `news_id` (`news_id` ASC) USING BTREE,
    INDEX `user_id` (`user_id` ASC) USING BTREE,
    CONSTRAINT `fresh_news_likes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `fresh_news_likes_ibfk_2` FOREIGN KEY (`news_id`) REFERENCES `fresh_news` (`fresh_news_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '新鲜事点赞表：记录用户对新鲜事的点赞行为'
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for fresh_news_tags
-- ----------------------------
DROP TABLE IF EXISTS `fresh_news_tags`;
CREATE TABLE `fresh_news_tags`
(
    `news_id`     bigint UNSIGNED NOT NULL COMMENT '新鲜事ID',
    `tag_id`      bigint UNSIGNED NOT NULL COMMENT '标签ID',
    `is_deleted`  tinyint(1)      NOT NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除',
    `create_time` timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`news_id`, `tag_id`) USING BTREE,
    INDEX `tag_id` (`tag_id` ASC) USING BTREE,
    CONSTRAINT `fresh_news_tags_ibfk_1` FOREIGN KEY (`news_id`) REFERENCES `fresh_news` (`fresh_news_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
    CONSTRAINT `fresh_news_tags_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '新鲜事标签关联表：存储新鲜事与标签之间的关联关系'
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for local_message
-- ----------------------------
DROP TABLE IF EXISTS `local_message`;
CREATE TABLE `local_message`
(
    `id`                 bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '本地消息唯一标识',
    `secure_invoke_json` json                                                          NOT NULL COMMENT '安全调用相关的 JSON 信息',
    `status`             tinyint(1)                                                    NOT NULL COMMENT '消息状态（例如待重试）',
    `retry_times`        int                                                           NOT NULL DEFAULT 0 COMMENT '重试次数',
    `next_retry_times`   datetime                                                      NOT NULL COMMENT '下次重试的时间',
    `fail_reason`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '失败原因',
    `create_time`        datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time`        datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`         tinyint                                                       NULL     DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
    `description`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '消息描述',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_next_retry_times` (`next_retry_times` ASC) USING BTREE COMMENT '按下次重试时间排序加速查询'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '本地消息表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`
(
    `notification_id`   bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `sender_id`         bigint UNSIGNED                                               NULL DEFAULT NULL COMMENT '发送者ID, 如果是系统通知可以为空',
    `receiver_id`       bigint UNSIGNED                                               NOT NULL COMMENT '接收者ID',
    `content`           varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
    `is_read`           tinyint                                                       NULL DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    `is_deleted`        tinyint                                                       NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `send_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    `create_time`       datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `description`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '通知描述',
    `notification_type` int                                                           NULL DEFAULT NULL COMMENT '\"通知类型，1为系统通知，2为收到的赞与收藏，3为@我，4为回复我的\"',
    PRIMARY KEY (`notification_id`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted` ASC) USING BTREE,
    INDEX `idx_is_read` (`is_read` ASC) USING BTREE,
    INDEX `idx_receiver_id` (`receiver_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_coin
-- ----------------------------
DROP TABLE IF EXISTS `post_coin`;
CREATE TABLE `post_coin`
(
    `coin_id`     int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT '投币ID',
    `post_id`     int UNSIGNED                                                  NOT NULL COMMENT '所属帖子ID',
    `user_id`     int UNSIGNED                                                  NOT NULL COMMENT '投币用户ID',
    `coin_amount` int UNSIGNED                                                  NOT NULL DEFAULT 1 COMMENT '投币数量',
    `create_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '投币时间',
    `update_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '投币更新时间',
    `is_deleted`  tinyint(1)                                                    NULL     DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '投币描述',
    PRIMARY KEY (`coin_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子投币表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_comment
-- ----------------------------
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment`
(
    `comment_id`        int UNSIGNED                                                   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `post_id`           int UNSIGNED                                                   NOT NULL COMMENT '所属帖子ID',
    `user_id`           int UNSIGNED                                                   NOT NULL COMMENT '评论用户ID',
    `parent_comment_id` int UNSIGNED                                                   NULL DEFAULT NULL COMMENT '父评论ID，表示是否回复别人的评论',
    `content`           varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
    `create_time`       datetime                                                       NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    `update_time`       datetime                                                       NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '评论更新时间',
    `is_deleted`        tinyint(1)                                                     NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `description`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL DEFAULT NULL COMMENT '评论描述',
    PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子评论表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_like
-- ----------------------------
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like`
(
    `like_id`     int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `post_id`     int UNSIGNED                                                  NOT NULL COMMENT '所属帖子ID',
    `user_id`     int UNSIGNED                                                  NOT NULL COMMENT '点赞用户ID',
    `create_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    `update_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '点赞更新时间',
    `is_deleted`  tinyint(1)                                                    NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '点赞描述',
    PRIMARY KEY (`like_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子点赞表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for report_comment
-- ----------------------------
DROP TABLE IF EXISTS `report_comment`;
CREATE TABLE `report_comment`
(
    `report_id`           bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    `comment_id`          bigint UNSIGNED                                               NOT NULL COMMENT '被举报的评论ID',
    `reporter_id`         bigint UNSIGNED                                               NOT NULL COMMENT '举报者的用户ID',
    `reason`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报原因',
    `report_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    `status`              tinyint(1)                                                    NULL DEFAULT 0 COMMENT '处理状态: 0-未处理, 1-已处理',
    `process_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理描述',
    `is_deleted`          tinyint(1)                                                    NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `create_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `description`         varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '举报表描述',
    `report_type`         varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '举报类型：涉政有害(\"涉政有害\"),     不友善(\"不友善\"),     垃圾广告(\"垃圾广告\"),     违法违规(\"违法违规\"),     色情低俗(\"色情低俗\"),     涉嫌侵权(\"涉嫌侵权\"),     网络暴力(\"网络暴力\"),     抄袭(\"抄袭\"),     自杀自残(\"自杀自残\"),     不实信息(\"不实信息\"),     其他(\"其他\");',
    `penalty_type`        int                                                           NULL DEFAULT NULL COMMENT ' 处理类型: 2-删除, 1-禁止评论, 0-不处罚',
    `process_time`        datetime                                                      NULL DEFAULT NULL COMMENT '处理时间',
    PRIMARY KEY (`report_id`) USING BTREE,
    INDEX `idx_comment_id` (`comment_id` ASC) USING BTREE,
    INDEX `idx_is_deleted_report_comment` (`is_deleted` ASC) USING BTREE,
    INDEX `idx_reporter_id` (`reporter_id` ASC) USING BTREE,
    INDEX `idx_status` (`status` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '举报评论表：用于存储用户举报评论的信息'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for report_fresh_news
-- ----------------------------
DROP TABLE IF EXISTS `report_fresh_news`;
CREATE TABLE `report_fresh_news`
(
    `report_id`           bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    `fresh_news_id`       bigint UNSIGNED                                               NOT NULL COMMENT '被举报的新鲜事ID',
    `reporter_id`         bigint UNSIGNED                                               NOT NULL COMMENT '举报者的用户ID',
    `reason`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报原因',
    `report_type`         varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL COMMENT '举报类型：涉政有害,     不友善,     垃圾广告,     违法违规,     色情低俗,     涉嫌侵权，     网络暴力,     抄袭,     自杀自残,     不实信息     其他',
    `report_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    `status`              tinyint(1)                                                    NULL DEFAULT 0 COMMENT '处理状态: 0-未处理, 1-已处理',
    `create_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`          tinyint(1)                                                    NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `process_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理描述',
    `penalty_type`        int                                                           NULL DEFAULT NULL COMMENT '处理类型 2-删除，1-禁止评论，0-不处罚',
    `process_time`        datetime                                                      NULL DEFAULT NULL COMMENT '处理时间',
    `description`         varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '举报表描述',
    PRIMARY KEY (`report_id`) USING BTREE,
    INDEX `idx_fresh_news_id` (`fresh_news_id` ASC) USING BTREE,
    INDEX `idx_is_deleted_report_fresh_news` (`is_deleted` ASC) USING BTREE,
    INDEX `idx_reporter_id` (`reporter_id` ASC) USING BTREE,
    INDEX `idx_status` (`status` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '举报新鲜事表：用于存储用户举报新鲜事的信息'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for report_post
-- ----------------------------
DROP TABLE IF EXISTS `report_post`;
CREATE TABLE `report_post`
(
    `report_id`           bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    `post_id`             bigint UNSIGNED                                               NOT NULL COMMENT '被举报的帖子ID',
    `reporter_id`         bigint UNSIGNED                                               NOT NULL COMMENT '举报者的用户ID',
    `reason`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报原因',
    `report_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    `status`              tinyint                                                       NULL DEFAULT 0 COMMENT '处理状态: 0-未处理, 1-已处理',
    `process_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理描述',
    `is_deleted`          tinyint                                                       NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `create_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `description`         varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '帖子举报表描述',
    PRIMARY KEY (`report_id`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted` ASC) USING BTREE,
    INDEX `idx_post_id` (`post_id` ASC) USING BTREE,
    INDEX `idx_reporter_id` (`reporter_id` ASC) USING BTREE,
    INDEX `idx_status` (`status` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子举报表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for report_user
-- ----------------------------
DROP TABLE IF EXISTS `report_user`;
CREATE TABLE `report_user`
(
    `report_id`           bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '举报ID',
    `reported_user_id`    bigint UNSIGNED                                               NOT NULL COMMENT '被举报的用户ID',
    `reporter_id`         bigint UNSIGNED                                               NOT NULL COMMENT '举报者的用户ID',
    `reason`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报原因',
    `report_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    `status`              tinyint                                                       NULL DEFAULT 0 COMMENT '处理状态: 0-未处理, 1-已处理',
    `process_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理描述',
    `is_deleted`          tinyint                                                       NULL DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `create_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `description`         varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户举报表描述',
    PRIMARY KEY (`report_id`) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted` ASC) USING BTREE,
    INDEX `idx_reported_user_id` (`reported_user_id` ASC) USING BTREE,
    INDEX `idx_reporter_id` (`reporter_id` ASC) USING BTREE,
    INDEX `idx_status` (`status` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户举报表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`
(
    `tag_id`      bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
    `is_deleted`  tinyint(1)                                                    NOT NULL DEFAULT 0 COMMENT '是否删除，0：未删除，1：已删除',
    `create_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp                                                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`tag_id`) USING BTREE,
    UNIQUE INDEX `idx_name_prefix` (`name`(191) ASC) USING BTREE,
    INDEX `idx_is_deleted` (`is_deleted` ASC) USING BTREE,
    INDEX `idx_is_deleted_tags` (`is_deleted` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '标签表：存储各类标签信息'
  ROW_FORMAT = COMPACT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `user_id`     int UNSIGNED                                                  NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
    `password`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码，经过加密存储',
    `mailbox`     varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
    `is_admin`    tinyint                                                       NULL DEFAULT 0 COMMENT '管理员权限: 0-普通用户, 1-运营组，2-开发组',
    `create_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `is_deleted`  tinyint                                                       NULL DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
    `is_banned`   tinyint                                                       NULL DEFAULT 0 COMMENT '是否封禁: 0-未封禁，1-已封禁',
    `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户描述',
    PRIMARY KEY (`user_id`) USING BTREE,
    UNIQUE INDEX `idx_username_is_deleted` (`username` ASC, `is_deleted` ASC) USING BTREE,
    INDEX `idx_is_banned_is_admin` (`is_banned` ASC, `is_admin` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户基础信息表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_feedback
-- ----------------------------
DROP TABLE IF EXISTS `user_feedback`;
CREATE TABLE `user_feedback`
(
    `feedback_id`   bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
    `user_id`       bigint UNSIGNED                                               NULL     DEFAULT NULL COMMENT '用户ID, 如果是匿名反馈可以为空',
    `feedback_type` tinyint                                                       NOT NULL COMMENT '反馈类型: 1-Bug反馈, 2-改进建议',
    `content`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反馈内容',
    `status`        tinyint                                                       NOT NULL DEFAULT 0 COMMENT '处理状态: 0-未处理, 1-处理中, 2-已解决, 3-已关闭',
    `is_deleted`    tinyint                                                       NULL     DEFAULT 0 COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_time`  datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time`  datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `description`   varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户反馈表描述',
    PRIMARY KEY (`feedback_id`) USING BTREE,
    INDEX `idx_status` (`status` ASC) USING BTREE,
    INDEX `idx_user_id` (`user_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户反馈表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_group_last_read
-- ----------------------------
DROP TABLE IF EXISTS `user_group_last_read`;
CREATE TABLE `user_group_last_read`
(
    `id`                   int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`              int UNSIGNED NOT NULL COMMENT '用户ID',
    `group_id`             int          NOT NULL COMMENT '群聊ID',
    `last_read_message_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户最后一次浏览的消息ID',
    `last_read_time`       datetime     NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后浏览时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_user_group` (`user_id` ASC, `group_id` ASC) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户群聊浏览状态表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_profile
-- ----------------------------
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile`
(
    `user_id`     int UNSIGNED                                                  NOT NULL COMMENT '用户ID',
    `avatar_url`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户头像URL',
    `username`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '登陆所用名字',
    `account`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户聊天发帖名字',
    `bio`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '个性标签/个人描述',
    `user_level`  tinyint UNSIGNED                                              NULL     DEFAULT 0 COMMENT '用户等级',
    `gender`      tinyint                                                       NULL     DEFAULT 2 COMMENT '性别: 0-男, 1-女, 2-其他',
    `grade`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '年级',
    `birth_date`  date                                                          NULL     DEFAULT NULL COMMENT '出生日期',
    `location`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '所在地',
    `website`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '个人网站或社交链接',
    `create_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`  tinyint                                                       NULL     DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户展示信息描述',
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户展示信息表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_session_list
-- ----------------------------
DROP TABLE IF EXISTS `user_session_list`;
CREATE TABLE `user_session_list`
(
    `id`          bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
    `uid`         int UNSIGNED                                                  NOT NULL COMMENT '用户 ID',
    `group_id`    int UNSIGNED                                                  NOT NULL COMMENT '群聊ID',
    `read_time`   datetime                                                      NULL DEFAULT NULL COMMENT '用户的最后阅读时间',
    `active_time` datetime                                                      NOT NULL COMMENT '会话的最后活跃时间',
    `last_msg_id` bigint                                                        NULL DEFAULT NULL COMMENT '会话的最后一条消息 ID',
    `create_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time` datetime                                                      NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`  tinyint                                                       NULL DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息描述',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_update_time_create_time` (`update_time` ASC, `create_time` ASC) USING BTREE COMMENT '活跃时间排序查询'
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户会话列表表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_stats
-- ----------------------------
DROP TABLE IF EXISTS `user_stats`;
CREATE TABLE `user_stats`
(
    `user_id`         int UNSIGNED                                                  NOT NULL COMMENT '用户ID',
    `account`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '用户聊天发帖名字',
    `student_number`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL     DEFAULT NULL COMMENT '学号，唯一',
    `article_count`   int                                                           NULL     DEFAULT 0 COMMENT '发表文章数',
    `comment_count`   int                                                           NULL     DEFAULT 0 COMMENT '评论次数',
    `statement_count` int                                                           NULL     DEFAULT 0 COMMENT '发言次数',
    `liked_count`     int                                                           NULL     DEFAULT 0 COMMENT '收到点赞次数',
    `coin_count`      int                                                           NULL     DEFAULT 0 COMMENT '硬币数量',
    `xp`              int                                                           NULL     DEFAULT 0 COMMENT '经验值',
    `quiz_type`       tinyint                                                       NULL     DEFAULT 0 COMMENT '考核通过状态',
    `last_login_time` datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近登录时间',
    `create_time`     datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time`     datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`      tinyint                                                       NULL     DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
    `description`     varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '用户动态数据描述',
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户动态数据表'
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for violation_record
-- ----------------------------
DROP TABLE IF EXISTS `violation_record`;
CREATE TABLE `violation_record`
(
    `id`             bigint UNSIGNED                                               NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`        int UNSIGNED                                                  NOT NULL COMMENT '违规用户ID',
    `violation_type` tinyint                                                       NOT NULL COMMENT '违规类型: 1-言论违规，2-行为违规，3-其他',
    `penalty_type`   tinyint                                                       NOT NULL DEFAULT 0 COMMENT '处罚类型: 0-无，1-警告，2-封禁，3-禁言',
    `penalty_status` tinyint                                                       NOT NULL DEFAULT 0 COMMENT '处罚状态: 0-未处罚, 1-处罚中, 2-处罚完成',
    `violation_time` datetime                                                      NOT NULL COMMENT '违规时间',
    `penalty_time`   datetime                                                      NULL     DEFAULT NULL COMMENT '处罚时间',
    `mute_duration`  int                                                           NULL     DEFAULT NULL COMMENT '禁言持续时间（分钟）',
    `ban_duration`   int                                                           NULL     DEFAULT NULL COMMENT '封禁持续时间（分钟）',
    `penalty_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL     DEFAULT NULL COMMENT '处罚原因',
    `create_time`    datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    `update_time`    datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`     tinyint                                                       NULL     DEFAULT 0 COMMENT '是否删除: 0-未删除，1-已删除',
    `description`    varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '违规行为描述',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_user_violation_id` (`user_id` ASC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户违规记录表'
  ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
