CREATE TABLE IF NOT EXISTS `apk_updates`
(
    `id`             INT UNSIGNED AUTO_INCREMENT COMMENT '记录ID'
        PRIMARY KEY,
    `version_code`   INT UNSIGNED                       NOT NULL COMMENT '版本代码',
    `version_name`   VARCHAR(50)                        NOT NULL COMMENT '版本名称',
    `download_url`   VARCHAR(255)                       NOT NULL COMMENT '下载链接',
    `update_message` TEXT                               NULL COMMENT '更新信息',
    `create_time`    DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '记录创建时间',
    `update_time`    DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间'
)
    COMMENT 'APK更新记录表' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_version_code`
    ON `apk_updates` ( `version_code` );

CREATE TABLE IF NOT EXISTS `chat_group`
(
    `group_id`          INT UNSIGNED AUTO_INCREMENT COMMENT '群聊ID'
        PRIMARY KEY,
    `group_name`        VARCHAR(255)                           NOT NULL COMMENT '群聊名称',
    `member_count`      INT UNSIGNED DEFAULT '0'               NOT NULL COMMENT '当前群聊人数',
    `member_limit`      INT UNSIGNED DEFAULT '100'             NOT NULL COMMENT '群聊人数限制',
    `type`              TINYINT                                NOT NULL COMMENT '群聊类型: 1-学习, 2-生活, 3-工具, 4-问题反馈, 5-社团, 6-比赛',
    `requires_approval` TINYINT(1)   DEFAULT 0                 NULL COMMENT '是否需要审核: 0-否, 1-是',
    `is_deleted`        TINYINT(1)   DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `is_banned`         TINYINT                                NULL COMMENT '是否封禁: 0-未封禁，1-已封禁',
    `avatar_url`        VARCHAR(255)                           NULL COMMENT '群聊头像URL',
    `background_url`    VARCHAR(255)                           NULL COMMENT '群聊背景图片URL',
    `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '群聊更新时间',
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '群聊创建时间',
    `description`       VARCHAR(500)                           NULL COMMENT '群聊描述'
)
    COMMENT '群聊表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `chat_group_announcement`
(
    `announcement_id` INT UNSIGNED AUTO_INCREMENT COMMENT '公告ID'
        PRIMARY KEY,
    `group_id`        INT UNSIGNED                         NOT NULL COMMENT '所属群聊ID',
    `user_id`         INT UNSIGNED                         NOT NULL COMMENT '发布用户ID',
    `title`           VARCHAR(255)                         NOT NULL COMMENT '公告标题',
    `content`         VARCHAR(2000)                        NOT NULL COMMENT '公告内容',
    `is_pinned`       TINYINT(1) DEFAULT 0                 NULL COMMENT '是否置顶公告: 1-置顶, 0-不置顶',
    `create_time`     DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '公告创建时间',
    `update_time`     DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '公告更新时间',
    `is_deleted`      TINYINT(1) DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `description`     VARCHAR(500)                         NULL COMMENT '公告描述'
)
    COMMENT '公告表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `chat_group_apply`
(
    `apply_id`       INT UNSIGNED AUTO_INCREMENT COMMENT '申请ID'
        PRIMARY KEY,
    `group_id`       INT UNSIGNED                           NOT NULL COMMENT '群聊ID',
    `user_id`        INT UNSIGNED                           NOT NULL COMMENT '申请用户ID',
    `apply_message`  VARCHAR(255) DEFAULT ''                NOT NULL COMMENT '申请理由或附加信息',
    `status`         TINYINT(1)   DEFAULT 0                 NOT NULL COMMENT '申请状态: 0-待审核, 1-已批准, 2-已拒绝',
    `apply_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '申请时间',
    `processed_time` DATETIME                               NULL COMMENT '处理时间',
    `processed_by`   INT UNSIGNED                           NULL COMMENT '处理申请的管理员ID',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '记录创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`     TINYINT      DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除，1-已删除',
    `description`    VARCHAR(500) DEFAULT ''                NOT NULL COMMENT '用户申请动态数据描述'
)
    COMMENT '用户申请加入群聊表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `chat_group_file`
(
    `file_id`     INT UNSIGNED AUTO_INCREMENT COMMENT '文件ID'
        PRIMARY KEY,
    `group_id`    INT UNSIGNED                         NOT NULL COMMENT '所属群聊ID',
    `user_id`     INT UNSIGNED                         NOT NULL COMMENT '上传用户ID',
    `file_name`   VARCHAR(255)                         NOT NULL COMMENT '文件名称',
    `file_size`   BIGINT UNSIGNED                      NOT NULL COMMENT '文件大小（字节）',
    `file_type`   TINYINT UNSIGNED                     NOT NULL COMMENT '文件类型: 1-图片, 2-文档, 3-视频, 4-音频, 5-其他',
    `file_url`    VARCHAR(500)                         NOT NULL COMMENT '文件存储的URL路径',
    `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '文件上传时间',
    `update_time` DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '文件更新时间',
    `is_deleted`  TINYINT(1) DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `description` VARCHAR(500)                         NULL COMMENT '文件描述'
)
    COMMENT '文件系统表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `chat_group_message`
(
    `message_id`      INT UNSIGNED AUTO_INCREMENT COMMENT '消息ID'
        PRIMARY KEY,
    `group_id`        INT                                  NOT NULL COMMENT '所属群聊ID',
    `sender_id`       INT UNSIGNED                         NOT NULL COMMENT '发送者用户ID',
    `receiver_id`     INT UNSIGNED                         NULL COMMENT '回复目标的用户ID（回复某人的消息）',
    `message_content` VARCHAR(1000)                        NULL COMMENT '聊天内容',
    `file_url`        VARCHAR(500)                         NULL COMMENT '文件存储的URL路径',
    `file_type`       TINYINT UNSIGNED                     NULL COMMENT '文件类型: 1-图片, 2-文档, 3-视频, 4-音频, 5-其他',
    `extra`           JSON                                 NULL COMMENT '额外消息内容',
    `gap_count`       INT                                  NULL COMMENT '消息间隔数',
    `create_time`     DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '消息发送时间',
    `update_time`     DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '消息更新时间',
    `is_deleted`      TINYINT(1) DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `description`     VARCHAR(500)                         NULL COMMENT '消息描述'
)
    COMMENT '群聊消息记录表' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_group_id`
    ON `chat_group_message` ( `group_id` );

CREATE INDEX `idx_receiver_id`
    ON `chat_group_message` ( `receiver_id` );

CREATE INDEX `idx_sender_id`
    ON `chat_group_message` ( `sender_id` );

CREATE TABLE IF NOT EXISTS `chat_group_message_read_info`
(
    `id`          INT UNSIGNED AUTO_INCREMENT COMMENT '记录ID'
        PRIMARY KEY,
    `group_id`    INT UNSIGNED                         NOT NULL COMMENT '群聊ID',
    `user_id`     INT UNSIGNED                         NOT NULL COMMENT '用户ID',
    `message_id`  INT UNSIGNED                         NOT NULL COMMENT '消息ID',
    `is_read`     TINYINT(1) DEFAULT 0                 NOT NULL COMMENT '消息是否已读: 0-未读, 1-已读',
    `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '记录创建时间',
    `update_time` DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间'
)
    COMMENT '用户未读消息表' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_group_user_message`
    ON `chat_group_message_read_info` ( `group_id`, `user_id`, `message_id` );

CREATE TABLE IF NOT EXISTS `chat_group_post`
(
    `post_id`     INT UNSIGNED AUTO_INCREMENT COMMENT '帖子ID'
        PRIMARY KEY,
    `group_id`    INT UNSIGNED                               NOT NULL COMMENT '所属群聊ID',
    `user_id`     INT UNSIGNED                               NOT NULL COMMENT '发布用户ID',
    `title`       VARCHAR(255)                               NOT NULL COMMENT '帖子标题',
    `content`     LONGTEXT                                   NOT NULL COMMENT '帖子内容',
    `category`    TINYINT UNSIGNED DEFAULT '0'               NOT NULL COMMENT '帖子类别: 0-general, 1-tutorial, 2-article, 3-experience',
    `is_pinned`   TINYINT(1)       DEFAULT 0                 NULL COMMENT '是否加精: 0-否, 1-是',
    `view_count`  INT UNSIGNED     DEFAULT '0'               NOT NULL COMMENT '浏览人数',
    `coin_count`  INT UNSIGNED     DEFAULT '0'               NOT NULL COMMENT '被投币量',
    `create_time` DATETIME         DEFAULT CURRENT_TIMESTAMP NULL COMMENT '帖子创建时间',
    `update_time` DATETIME         DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '帖子更新时间',
    `is_deleted`  TINYINT(1)       DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `description` VARCHAR(500)                               NULL COMMENT '帖子描述'
)
    COMMENT '知识区帖子表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `chat_group_user`
(
    `id`                INT UNSIGNED AUTO_INCREMENT COMMENT 'ID'
        PRIMARY KEY,
    `group_id`          INT UNSIGNED                             NOT NULL COMMENT '群聊ID',
    `user_id`           INT UNSIGNED                             NOT NULL COMMENT '用户ID',
    `joined_time`       DATETIME   DEFAULT CURRENT_TIMESTAMP     NOT NULL COMMENT '用户加入群聊时间',
    `role`              TINYINT(1) DEFAULT 0                     NOT NULL COMMENT '用户角色: 0-普通成员, 1-管理员, 2-群主',
    `is_deleted`        TINYINT(1) DEFAULT 0                     NOT NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `is_muted`          TINYINT(1) DEFAULT 0                     NOT NULL COMMENT '是否在该群聊被禁言: 0-未禁言, 1-已禁言',
    `mute_start_time`   DATETIME   DEFAULT '1970-01-01 00:00:00' NOT NULL COMMENT '禁言开始时间',
    `mute_duration`     INT        DEFAULT 0                     NOT NULL COMMENT '禁言持续时间（分钟）',
    `create_time`       DATETIME   DEFAULT CURRENT_TIMESTAMP     NOT NULL COMMENT '记录创建时间',
    `update_time`       DATETIME   DEFAULT CURRENT_TIMESTAMP     NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `join_status`       TINYINT(1) DEFAULT 1                     NOT NULL COMMENT '用户入群状态: 0-审核中, 1-已入群, 2-审核拒绝',
    `join_request_info` VARCHAR(500)                             NULL COMMENT '用户入群申请信息',
    `description`       VARCHAR(500)                             NULL COMMENT '用户-群聊关联表描述',
    CONSTRAINT `idx_user_group`
        UNIQUE ( `group_id`, `user_id` )
)
    COMMENT '用户-群聊关联表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `chat_group_user_display_name`
(
    `id`           INT UNSIGNED AUTO_INCREMENT COMMENT '记录ID'
        PRIMARY KEY,
    `group_id`     INT UNSIGNED                         NOT NULL COMMENT '群聊ID',
    `user_id`      INT UNSIGNED                         NOT NULL COMMENT '用户ID',
    `display_name` VARCHAR(255)                         NOT NULL COMMENT '用户在该群聊中的显示名称',
    `create_time`  DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '记录创建时间',
    `update_time`  DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`   TINYINT(1) DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    CONSTRAINT `idx_group_user`
        UNIQUE ( `group_id`, `user_id` ) COMMENT '确保每个用户在一个群聊中只有一个显示名称'
)
    COMMENT '用户群聊显示名称表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `comment_like`
(
    `like_id`     INT UNSIGNED AUTO_INCREMENT COMMENT '点赞ID'
        PRIMARY KEY,
    `comment_id`  INT UNSIGNED                         NOT NULL COMMENT '所属评论ID',
    `user_id`     INT UNSIGNED                         NOT NULL COMMENT '点赞用户ID',
    `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '点赞时间',
    `update_time` DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '点赞更新时间',
    `is_deleted`  TINYINT(1) DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `description` VARCHAR(500)                         NULL COMMENT '点赞描述'
)
    COMMENT '评论点赞表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `fresh_news`
(
    `fresh_news_id`   BIGINT UNSIGNED AUTO_INCREMENT COMMENT '新鲜事ID'
        PRIMARY KEY,
    `user_id`         INT UNSIGNED                            NOT NULL COMMENT '发布用户id',
    `title`           VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
    `content`         TEXT COLLATE utf8mb4_unicode_ci         NOT NULL COMMENT '内容',
    `images`          VARCHAR(2048)                           NULL COMMENT '新鲜事的图片，最多9张，图片路径以逗号分隔',
    `tags`            VARCHAR(1024)                           NULL COMMENT '新鲜事的标签，多个标签以逗号分隔',
    `liked`           INT UNSIGNED DEFAULT '0'                NULL COMMENT '点赞数量',
    `comments`        INT UNSIGNED DEFAULT '0'                NULL COMMENT '评论数量',
    `create_time`     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP  NOT NULL COMMENT '创建时间',
    `update_time`     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP  NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT(1)   DEFAULT 0                  NOT NULL COMMENT '是否删除，0：未删除，1：已删除',
    `allow_comments`  TINYINT(1)   DEFAULT 1                  NOT NULL COMMENT '是否允许评论，0：不允许，1：允许',
    `favorites_count` INT          DEFAULT 0                  NOT NULL COMMENT '被收藏数'
)
    COMMENT '新鲜事表：存储用户发布的各类新鲜事内容及相关信息' COLLATE = utf8mb4_general_ci
                                                              ROW_FORMAT = COMPACT;

CREATE INDEX `idx_is_deleted_fresh_news`
    ON `fresh_news` ( `is_deleted` );

CREATE INDEX `idx_user_deleted`
    ON `fresh_news` ( `user_id`, `is_deleted` );

CREATE TABLE IF NOT EXISTS `local_message`
(
    `id`                 BIGINT AUTO_INCREMENT COMMENT '本地消息唯一标识'
        PRIMARY KEY,
    `secure_invoke_json` JSON                               NOT NULL COMMENT '安全调用相关的 JSON 信息',
    `status`             TINYINT(1)                         NOT NULL COMMENT '消息状态（例如待重试）',
    `retry_times`        INT      DEFAULT 0                 NOT NULL COMMENT '重试次数',
    `next_retry_times`   DATETIME                           NOT NULL COMMENT '下次重试的时间',
    `fail_reason`        VARCHAR(500)                       NULL COMMENT '失败原因',
    `create_time`        DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '记录创建时间',
    `update_time`        DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`         TINYINT  DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除，1-已删除',
    `description`        VARCHAR(500)                       NULL COMMENT '消息描述'
)
    COMMENT '本地消息表' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_next_retry_times`
    ON `local_message` ( `next_retry_times` )
    COMMENT '按下次重试时间排序加速查询';

CREATE TABLE IF NOT EXISTS `notification`
(
    `notification_id`   BIGINT UNSIGNED AUTO_INCREMENT COMMENT '通知ID'
        PRIMARY KEY,
    `sender_id`         BIGINT UNSIGNED                    NULL COMMENT '发送者ID, 如果是系统通知可以为空',
    `receiver_id`       BIGINT UNSIGNED                    NOT NULL COMMENT '接收者ID',
    `content`           VARCHAR(500)                       NOT NULL COMMENT '通知内容',
    `is_read`           TINYINT  DEFAULT 0                 NULL COMMENT '是否已读: 0-未读, 1-已读',
    `is_deleted`        TINYINT  DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `send_time`         DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '发送时间',
    `create_time`       DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    `update_time`       DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `description`       VARCHAR(500)                       NULL COMMENT '通知描述',
    `notification_type` INT                                NULL COMMENT '"通知类型，1为系统通知，2为收到的赞与收藏，3为@我，4为回复我的"'
)
    COMMENT '通知表' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_is_deleted`
    ON `notification` ( `is_deleted` );

CREATE INDEX `idx_is_read`
    ON `notification` ( `is_read` );

CREATE INDEX `idx_receiver_id`
    ON `notification` ( `receiver_id` );

CREATE TABLE IF NOT EXISTS `post_coin`
(
    `coin_id`     INT UNSIGNED AUTO_INCREMENT COMMENT '投币ID'
        PRIMARY KEY,
    `post_id`     INT UNSIGNED                           NOT NULL COMMENT '所属帖子ID',
    `user_id`     INT UNSIGNED                           NOT NULL COMMENT '投币用户ID',
    `coin_amount` INT UNSIGNED DEFAULT '1'               NOT NULL COMMENT '投币数量',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '投币时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '投币更新时间',
    `is_deleted`  TINYINT(1)   DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `description` VARCHAR(500)                           NULL COMMENT '投币描述'
)
    COMMENT '帖子投币表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `post_comment`
(
    `comment_id`        INT UNSIGNED AUTO_INCREMENT COMMENT '评论ID'
        PRIMARY KEY,
    `post_id`           INT UNSIGNED                         NOT NULL COMMENT '所属帖子ID',
    `user_id`           INT UNSIGNED                         NOT NULL COMMENT '评论用户ID',
    `parent_comment_id` INT UNSIGNED                         NULL COMMENT '父评论ID，表示是否回复别人的评论',
    `content`           VARCHAR(1000)                        NOT NULL COMMENT '评论内容',
    `create_time`       DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '评论时间',
    `update_time`       DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '评论更新时间',
    `is_deleted`        TINYINT(1) DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `description`       VARCHAR(500)                         NULL COMMENT '评论描述'
)
    COMMENT '帖子评论表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `post_like`
(
    `like_id`     INT UNSIGNED AUTO_INCREMENT COMMENT '点赞ID'
        PRIMARY KEY,
    `post_id`     INT UNSIGNED                         NOT NULL COMMENT '所属帖子ID',
    `user_id`     INT UNSIGNED                         NOT NULL COMMENT '点赞用户ID',
    `create_time` DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '点赞时间',
    `update_time` DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '点赞更新时间',
    `is_deleted`  TINYINT(1) DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `description` VARCHAR(500)                         NULL COMMENT '点赞描述'
)
    COMMENT '帖子点赞表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `report_comment`
(
    `report_id`           BIGINT UNSIGNED AUTO_INCREMENT COMMENT '举报ID'
        PRIMARY KEY,
    `comment_id`          BIGINT UNSIGNED                      NOT NULL COMMENT '被举报的评论ID',
    `reporter_id`         BIGINT UNSIGNED                      NOT NULL COMMENT '举报者的用户ID',
    `reason`              VARCHAR(255)                         NOT NULL COMMENT '举报原因',
    `report_time`         DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '举报时间',
    `status`              TINYINT(1) DEFAULT 0                 NULL COMMENT '处理状态: 0-未处理, 1-已处理',
    `process_description` VARCHAR(500)                         NULL COMMENT '处理描述',
    `is_deleted`          TINYINT(1) DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `create_time`         DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    `update_time`         DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `description`         VARCHAR(500)                         NULL COMMENT '举报表描述',
    `report_type`         VARCHAR(10)                          NOT NULL COMMENT '举报类型：涉政有害("涉政有害"),     不友善("不友善"),     垃圾广告("垃圾广告"),     违法违规("违法违规"),     色情低俗("色情低俗"),     涉嫌侵权("涉嫌侵权"),     网络暴力("网络暴力"),     抄袭("抄袭"),     自杀自残("自杀自残"),     不实信息("不实信息"),     其他("其他");',
    `penalty_type`        INT                                  NULL COMMENT ' 处理类型: 2-删除, 1-禁止评论, 0-不处罚',
    `process_time`        DATETIME                             NULL COMMENT '处理时间'
)
    COMMENT '举报评论表：用于存储用户举报评论的信息' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_comment_id`
    ON `report_comment` ( `comment_id` );

CREATE INDEX `idx_is_deleted_report_comment`
    ON `report_comment` ( `is_deleted` );

CREATE INDEX `idx_reporter_id`
    ON `report_comment` ( `reporter_id` );

CREATE INDEX `idx_status`
    ON `report_comment` ( `status` );

CREATE TABLE IF NOT EXISTS `report_fresh_news`
(
    `report_id`           BIGINT UNSIGNED AUTO_INCREMENT COMMENT '举报ID'
        PRIMARY KEY,
    `fresh_news_id`       BIGINT UNSIGNED                      NOT NULL COMMENT '被举报的新鲜事ID',
    `reporter_id`         BIGINT UNSIGNED                      NOT NULL COMMENT '举报者的用户ID',
    `reason`              VARCHAR(255)                         NOT NULL COMMENT '举报原因',
    `report_type`         VARCHAR(10)                          NOT NULL COMMENT '举报类型：涉政有害,     不友善,     垃圾广告,     违法违规,     色情低俗,     涉嫌侵权，     网络暴力,     抄袭,     自杀自残,     不实信息     其他',
    `report_time`         DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '举报时间',
    `status`              TINYINT(1) DEFAULT 0                 NULL COMMENT '处理状态: 0-未处理, 1-已处理',
    `create_time`         DATETIME   DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    `update_time`         DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`          TINYINT(1) DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `process_description` VARCHAR(500)                         NULL COMMENT '处理描述',
    `penalty_type`        INT                                  NULL COMMENT '处理类型 2-删除，1-禁止评论，0-不处罚',
    `process_time`        DATETIME                             NULL COMMENT '处理时间',
    `description`         VARCHAR(500)                         NULL COMMENT '举报表描述'
)
    COMMENT '举报新鲜事表：用于存储用户举报新鲜事的信息' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_fresh_news_id`
    ON `report_fresh_news` ( `fresh_news_id` );

CREATE INDEX `idx_is_deleted_report_fresh_news`
    ON `report_fresh_news` ( `is_deleted` );

CREATE INDEX `idx_reporter_id`
    ON `report_fresh_news` ( `reporter_id` );

CREATE INDEX `idx_status`
    ON `report_fresh_news` ( `status` );

CREATE TABLE IF NOT EXISTS `report_post`
(
    `report_id`           BIGINT UNSIGNED AUTO_INCREMENT COMMENT '举报ID'
        PRIMARY KEY,
    `post_id`             BIGINT UNSIGNED                    NOT NULL COMMENT '被举报的帖子ID',
    `reporter_id`         BIGINT UNSIGNED                    NOT NULL COMMENT '举报者的用户ID',
    `reason`              VARCHAR(255)                       NOT NULL COMMENT '举报原因',
    `report_time`         DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '举报时间',
    `status`              TINYINT  DEFAULT 0                 NULL COMMENT '处理状态: 0-未处理, 1-已处理',
    `process_description` VARCHAR(500)                       NULL COMMENT '处理描述',
    `is_deleted`          TINYINT  DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `create_time`         DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    `update_time`         DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `description`         VARCHAR(500)                       NULL COMMENT '帖子举报表描述'
)
    COMMENT '帖子举报表' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_is_deleted`
    ON `report_post` ( `is_deleted` );

CREATE INDEX `idx_post_id`
    ON `report_post` ( `post_id` );

CREATE INDEX `idx_reporter_id`
    ON `report_post` ( `reporter_id` );

CREATE INDEX `idx_status`
    ON `report_post` ( `status` );

CREATE TABLE IF NOT EXISTS `report_user`
(
    `report_id`           BIGINT UNSIGNED AUTO_INCREMENT COMMENT '举报ID'
        PRIMARY KEY,
    `reported_user_id`    BIGINT UNSIGNED                    NOT NULL COMMENT '被举报的用户ID',
    `reporter_id`         BIGINT UNSIGNED                    NOT NULL COMMENT '举报者的用户ID',
    `reason`              VARCHAR(255)                       NOT NULL COMMENT '举报原因',
    `report_time`         DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '举报时间',
    `status`              TINYINT  DEFAULT 0                 NULL COMMENT '处理状态: 0-未处理, 1-已处理',
    `process_description` VARCHAR(500)                       NULL COMMENT '处理描述',
    `is_deleted`          TINYINT  DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `create_time`         DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    `update_time`         DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `description`         VARCHAR(500)                       NULL COMMENT '用户举报表描述'
)
    COMMENT '用户举报表' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_is_deleted`
    ON `report_user` ( `is_deleted` );

CREATE INDEX `idx_reported_user_id`
    ON `report_user` ( `reported_user_id` );

CREATE INDEX `idx_reporter_id`
    ON `report_user` ( `reporter_id` );

CREATE INDEX `idx_status`
    ON `report_user` ( `status` );

CREATE TABLE IF NOT EXISTS `tags`
(
    `tag_id`      BIGINT UNSIGNED AUTO_INCREMENT COMMENT '标签ID'
        PRIMARY KEY,
    `name`        VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
    `is_deleted`  TINYINT(1) DEFAULT 0                    NOT NULL COMMENT '是否删除，0：未删除，1：已删除',
    `create_time` TIMESTAMP  DEFAULT CURRENT_TIMESTAMP    NOT NULL COMMENT '创建时间',
    `update_time` TIMESTAMP  DEFAULT CURRENT_TIMESTAMP    NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT `idx_name_prefix`
        UNIQUE ( `name`( 191 ) )
)
    COMMENT '标签表：存储各类标签信息' COLLATE = utf8mb4_general_ci
                                      ROW_FORMAT = COMPACT;

CREATE TABLE IF NOT EXISTS `fresh_news_tags`
(
    `news_id`     BIGINT UNSIGNED                      NOT NULL COMMENT '新鲜事ID',
    `tag_id`      BIGINT UNSIGNED                      NOT NULL COMMENT '标签ID',
    `is_deleted`  TINYINT(1) DEFAULT 0                 NOT NULL COMMENT '是否删除，0：未删除，1：已删除',
    `create_time` TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    PRIMARY KEY ( `news_id`, `tag_id` ),
    CONSTRAINT `fresh_news_tags_ibfk_1`
        FOREIGN KEY ( `news_id` ) REFERENCES `fresh_news` ( `fresh_news_id` )
            ON DELETE CASCADE,
    CONSTRAINT `fresh_news_tags_ibfk_2`
        FOREIGN KEY ( `tag_id` ) REFERENCES `tags` ( `tag_id` )
            ON DELETE CASCADE
)
    COMMENT '新鲜事标签关联表：存储新鲜事与标签之间的关联关系' COLLATE = utf8mb4_general_ci
                                                              ROW_FORMAT = COMPACT;

CREATE INDEX `tag_id`
    ON `fresh_news_tags` ( `tag_id` );

CREATE INDEX `idx_is_deleted`
    ON `tags` ( `is_deleted` );

CREATE INDEX `idx_is_deleted_tags`
    ON `tags` ( `is_deleted` );

CREATE TABLE IF NOT EXISTS `user`
(
    `user_id`     INT UNSIGNED AUTO_INCREMENT COMMENT '用户ID'
        PRIMARY KEY,
    `username`    VARCHAR(255)                       NOT NULL COMMENT '用户名',
    `password`    VARCHAR(128)                       NOT NULL COMMENT '密码，经过加密存储',
    `mailbox`     VARCHAR(128)                       NOT NULL COMMENT '邮箱',
    `is_admin`    TINYINT  DEFAULT 0                 NULL COMMENT '管理员权限: 0-普通用户, 1-运营组，2-开发组',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `is_deleted`  TINYINT  DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除，1-已删除',
    `is_banned`   TINYINT  DEFAULT 0                 NULL COMMENT '是否封禁: 0-未封禁，1-已封禁',
    `description` VARCHAR(255)                       NULL COMMENT '用户描述',
    CONSTRAINT `idx_username_is_deleted`
        UNIQUE ( `username`, `is_deleted` )
)
    COMMENT '用户基础信息表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `fresh_news_comments`
(
    `comment_id`  BIGINT UNSIGNED AUTO_INCREMENT COMMENT '评论ID'
        PRIMARY KEY,
    `user_id`     INT UNSIGNED                              NOT NULL COMMENT '评论用户id',
    `news_id`     BIGINT UNSIGNED                           NOT NULL COMMENT '新鲜事ID',
    `root`        INT             DEFAULT 0                 NULL,
    `parent_id`   BIGINT UNSIGNED DEFAULT '0'               NOT NULL COMMENT '一级评论ID，如果是子评论则为父评论ID',
    `content`     VARCHAR(1024)                             NOT NULL COMMENT '评论内容',
    `liked`       INT UNSIGNED    DEFAULT '0'               NULL COMMENT '点赞数量',
    `is_deleted`  INT             DEFAULT 0                 NULL COMMENT '是否删除',
    `create_time` TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time` TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT `fresh_news_comments_ibfk_1`
        FOREIGN KEY ( `news_id` ) REFERENCES `fresh_news` ( `fresh_news_id` )
            ON DELETE CASCADE,
    CONSTRAINT `fresh_news_comments_ibfk_2`
        FOREIGN KEY ( `user_id` ) REFERENCES `user` ( `user_id` )
            ON DELETE CASCADE
)
    COMMENT '新鲜事评论表：存储用户对新鲜事的评论内容' ROW_FORMAT = COMPACT;

CREATE INDEX `news_id`
    ON `fresh_news_comments` ( `news_id` );

CREATE INDEX `user_id`
    ON `fresh_news_comments` ( `user_id` );

CREATE TABLE IF NOT EXISTS `fresh_news_favorites`
(
    `favorites_id` BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键：唯一标识每条收藏记录'
        PRIMARY KEY,
    `user_id`      INT UNSIGNED                         NOT NULL COMMENT '用户ID，关联用户表，表示收藏该新鲜事的用户',
    `news_id`      BIGINT UNSIGNED                      NOT NULL COMMENT '新鲜事ID，关联新鲜事表，表示被收藏的新鲜事',
    `create_time`  TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '收藏时间，记录用户收藏该新鲜事的时间',
    `is_deleted`   TINYINT(1) DEFAULT 0                 NOT NULL COMMENT '是否删除，0：未删除，1：已删除，逻辑删除字段',
    CONSTRAINT `fresh_news_favorites_ibfk_1`
        FOREIGN KEY ( `user_id` ) REFERENCES `user` ( `user_id` )
            ON DELETE CASCADE,
    CONSTRAINT `fresh_news_favorites_ibfk_2`
        FOREIGN KEY ( `news_id` ) REFERENCES `fresh_news` ( `fresh_news_id` )
            ON DELETE CASCADE
)
    COMMENT '收藏功能表：存储用户收藏的新鲜事信息' COLLATE = utf8mb4_general_ci
                                                  ROW_FORMAT = COMPACT;

CREATE INDEX `idx_is_deleted_favorites`
    ON `fresh_news_favorites` ( `is_deleted` )
    COMMENT '索引：加速基于删除状态的查询，便于执行逻辑删除操作';

CREATE INDEX `idx_user_news_id`
    ON `fresh_news_favorites` ( `user_id`, `news_id` )
    COMMENT '联合索引：加速用户和新鲜事的查询，可以快速查询某个用户收藏了哪些新鲜事';

CREATE INDEX `news_id`
    ON `fresh_news_favorites` ( `news_id` );

CREATE TABLE IF NOT EXISTS `fresh_news_likes`
(
    `like_id`     BIGINT UNSIGNED AUTO_INCREMENT COMMENT '点赞ID'
        PRIMARY KEY,
    `user_id`     INT UNSIGNED                         NOT NULL COMMENT '点赞用户id',
    `news_id`     BIGINT UNSIGNED                      NOT NULL COMMENT '新鲜事ID',
    `is_deleted`  TINYINT(1) DEFAULT 0                 NOT NULL COMMENT '是否删除，0：未删除，1：已删除',
    `create_time` TIMESTAMP  DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '点赞时间',
    CONSTRAINT `fresh_news_likes_ibfk_1`
        FOREIGN KEY ( `user_id` ) REFERENCES `user` ( `user_id` )
            ON DELETE CASCADE,
    CONSTRAINT `fresh_news_likes_ibfk_2`
        FOREIGN KEY ( `news_id` ) REFERENCES `fresh_news` ( `fresh_news_id` )
            ON DELETE CASCADE
)
    COMMENT '新鲜事点赞表：记录用户对新鲜事的点赞行为' ROW_FORMAT = COMPACT;

CREATE INDEX `idx_is_deleted_likes`
    ON `fresh_news_likes` ( `is_deleted` );

CREATE INDEX `news_id`
    ON `fresh_news_likes` ( `news_id` );

CREATE INDEX `user_id`
    ON `fresh_news_likes` ( `user_id` );

CREATE INDEX `idx_is_banned_is_admin`
    ON `user` ( `is_banned`, `is_admin` );

CREATE TABLE IF NOT EXISTS `user_feedback`
(
    `feedback_id`   BIGINT UNSIGNED AUTO_INCREMENT COMMENT '反馈ID'
        PRIMARY KEY,
    `user_id`       BIGINT UNSIGNED                    NULL COMMENT '用户ID, 如果是匿名反馈可以为空',
    `feedback_type` TINYINT                            NOT NULL COMMENT '反馈类型: 1-Bug反馈, 2-改进建议',
    `content`       VARCHAR(500)                       NOT NULL COMMENT '反馈内容',
    `status`        TINYINT  DEFAULT 0                 NOT NULL COMMENT '处理状态: 0-未处理, 1-处理中, 2-已解决, 3-已关闭',
    `is_deleted`    TINYINT  DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除, 1-已删除',
    `created_time`  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    `updated_time`  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `description`   VARCHAR(500)                       NULL COMMENT '用户反馈表描述'
)
    COMMENT '用户反馈表' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_status`
    ON `user_feedback` ( `status` );

CREATE INDEX `idx_user_id`
    ON `user_feedback` ( `user_id` );

CREATE TABLE IF NOT EXISTS `user_group_last_read`
(
    `id`                   INT UNSIGNED AUTO_INCREMENT COMMENT '记录ID'
        PRIMARY KEY,
    `user_id`              INT UNSIGNED                       NOT NULL COMMENT '用户ID',
    `group_id`             INT                                NOT NULL COMMENT '群聊ID',
    `last_read_message_id` INT UNSIGNED                       NULL COMMENT '用户最后一次浏览的消息ID',
    `last_read_time`       DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '最后浏览时间',
    CONSTRAINT `idx_user_group`
        UNIQUE ( `user_id`, `group_id` )
)
    COMMENT '用户群聊浏览状态表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `user_profile`
(
    `user_id`     INT UNSIGNED                               NOT NULL COMMENT '用户ID'
        PRIMARY KEY,
    `avatar_url`  VARCHAR(255)                               NULL COMMENT '用户头像URL',
    `username`    VARCHAR(255)                               NULL COMMENT '登陆所用名字',
    `account`     VARCHAR(255)                               NULL COMMENT '用户聊天发帖名字',
    `bio`         VARCHAR(255)                               NULL COMMENT '个性标签/个人描述',
    `user_level`  TINYINT UNSIGNED DEFAULT '0'               NULL COMMENT '用户等级',
    `gender`      TINYINT          DEFAULT 2                 NULL COMMENT '性别: 0-男, 1-女, 2-其他',
    `grade`       VARCHAR(50)                                NULL COMMENT '年级',
    `birth_date`  DATE                                       NULL COMMENT '出生日期',
    `location`    VARCHAR(255)                               NULL COMMENT '所在地',
    `website`     VARCHAR(255)                               NULL COMMENT '个人网站或社交链接',
    `create_time` DATETIME         DEFAULT CURRENT_TIMESTAMP NULL COMMENT '记录创建时间',
    `update_time` DATETIME         DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`  TINYINT          DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除，1-已删除',
    `description` VARCHAR(500)     DEFAULT ''                NOT NULL COMMENT '用户展示信息描述'
)
    COMMENT '用户展示信息表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `user_session_list`
(
    `id`          BIGINT UNSIGNED AUTO_INCREMENT COMMENT '唯一标识'
        PRIMARY KEY,
    `uid`         INT UNSIGNED                       NOT NULL COMMENT '用户 ID',
    `group_id`    INT UNSIGNED                       NOT NULL COMMENT '群聊ID',
    `read_time`   DATETIME                           NULL COMMENT '用户的最后阅读时间',
    `active_time` DATETIME                           NOT NULL COMMENT '会话的最后活跃时间',
    `last_msg_id` BIGINT                             NULL COMMENT '会话的最后一条消息 ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '记录创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`  TINYINT  DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除，1-已删除',
    `description` VARCHAR(500)                       NULL COMMENT '消息描述'
)
    COMMENT '用户会话列表表' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_update_time_create_time`
    ON `user_session_list` ( `update_time`, `create_time` )
    COMMENT '活跃时间排序查询';

CREATE TABLE IF NOT EXISTS `user_stats`
(
    `user_id`         INT UNSIGNED                           NOT NULL COMMENT '用户ID'
        PRIMARY KEY,
    `account`         VARCHAR(255)                           NULL COMMENT '用户聊天发帖名字',
    `student_number`  VARCHAR(50)                            NULL COMMENT '学号，唯一',
    `article_count`   INT          DEFAULT 0                 NULL COMMENT '发表文章数',
    `comment_count`   INT          DEFAULT 0                 NULL COMMENT '评论次数',
    `statement_count` INT          DEFAULT 0                 NULL COMMENT '发言次数',
    `liked_count`     INT          DEFAULT 0                 NULL COMMENT '收到点赞次数',
    `coin_count`      INT          DEFAULT 0                 NULL COMMENT '硬币数量',
    `xp`              INT          DEFAULT 0                 NULL COMMENT '经验值',
    `quiz_type`       TINYINT      DEFAULT 0                 NULL COMMENT '考核通过状态',
    `last_login_time` DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最近登录时间',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '记录创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`      TINYINT      DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除，1-已删除',
    `description`     VARCHAR(500) DEFAULT ''                NOT NULL COMMENT '用户动态数据描述'
)
    COMMENT '用户动态数据表' ROW_FORMAT = DYNAMIC;

CREATE TABLE IF NOT EXISTS `violation_record`
(
    `id`             BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID'
        PRIMARY KEY,
    `user_id`        INT UNSIGNED                           NOT NULL COMMENT '违规用户ID',
    `violation_type` TINYINT                                NOT NULL COMMENT '违规类型: 1-言论违规，2-行为违规，3-其他',
    `penalty_type`   TINYINT      DEFAULT 0                 NOT NULL COMMENT '处罚类型: 0-无，1-警告，2-封禁，3-禁言',
    `penalty_status` TINYINT      DEFAULT 0                 NOT NULL COMMENT '处罚状态: 0-未处罚, 1-处罚中, 2-处罚完成',
    `violation_time` DATETIME                               NOT NULL COMMENT '违规时间',
    `penalty_time`   DATETIME                               NULL COMMENT '处罚时间',
    `mute_duration`  INT                                    NULL COMMENT '禁言持续时间（分钟）',
    `ban_duration`   INT                                    NULL COMMENT '封禁持续时间（分钟）',
    `penalty_reason` VARCHAR(255)                           NULL COMMENT '处罚原因',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP NULL COMMENT '记录创建时间',
    `update_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    `is_deleted`     TINYINT      DEFAULT 0                 NULL COMMENT '是否删除: 0-未删除，1-已删除',
    `description`    VARCHAR(500) DEFAULT ''                NOT NULL COMMENT '违规行为描述'
)
    COMMENT '用户违规记录表' ROW_FORMAT = DYNAMIC;

CREATE INDEX `idx_user_violation_id`
    ON `violation_record` ( `user_id` );


