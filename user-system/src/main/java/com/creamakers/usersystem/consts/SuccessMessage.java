package com.creamakers.usersystem.consts;




/**
 * @author yuxialuozi
 * @data 2024/10/24 - 19:19
 * @description 常用的群聊应用成功信息
 */
public class SuccessMessage {

    // 用户相关成功信息
    public static final String USER_REGISTERED = "用户注册成功";
    public static final String USER_LOGGED_IN = "用户登录成功";
    public static final String USER_UPDATED = "用户信息更新成功";
    public static final String USER_LOGGED_OUT = "用户注销成功";
    public static final String USER_TOKEN_REFRESH = "用户Token刷新成功";

    // 群聊相关成功信息
    public static final String GROUP_CREATED = "群聊创建成功";
    public static final String GROUP_UPDATED = "群聊信息更新成功";
    public static final String USER_ADDED_TO_GROUP = "用户成功加入群聊";
    public static final String USER_REMOVED_FROM_GROUP = "用户成功移出群聊";

    // 消息相关成功信息
    public static final String MESSAGE_SENT = "消息发送成功";
    public static final String MESSAGE_DELETED = "消息删除成功";
    public static final String MESSAGE_RETRIEVED = "消息获取成功";

    // 权限相关成功信息
    public static final String PERMISSION_GRANTED = "权限授予成功";
    public static final String ACTION_ALLOWED = "操作允许";

    // 通用成功信息
    public static final String OPERATION_SUCCESSFUL = "操作成功";
    public static final String DATA_RETRIEVED = "数据获取成功";
    public static final String NO_VIOLATION_INFO = "没有违规记录哦";
    public static final String TOKEN_REFRESHED = "Token已经刷新完毕";
    public static final String USER_NOT_EXITS = "用户验证通过";

    //apk成功消息
    public final static String ALREADY_LATEST_VERSION_MESSAGE = "当前已是最新版本，无需更新。";
    public final static String FETCH_LATEST_APK_VERSION_SUCCESS_MESSAGE = "获取最新apk版本成功";
    public final static String FETCH_LATEST_APK_VERSION_FAILURE_MESSAGE = "获取最新apk版本失败，请练习后端人员";


    public static final String VERIFICATION_CODE_SENT = "验证码已发送";
}
