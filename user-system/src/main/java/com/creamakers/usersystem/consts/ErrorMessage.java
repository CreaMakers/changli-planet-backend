package com.creamakers.usersystem.consts;


/**
 * @author yuxialuozi
 * @data 2024/10/24 - 19:02
 * @description 常用的群聊应用错误信息
 */
public class ErrorMessage {

    // 用户相关错误
    public static final String USER_NOT_FOUND = "用户未找到";
    public static final String USER_ALREADY_EXISTS = "用户已存在";
    public static final String INVALID_CREDENTIALS = "无效的凭证";
    public static final String USERNAME_TOO_SHORT = "用户名过短，至少需要 3 个字符";
    public static final String PASSWORD_TOO_WEAK = "密码强度不足，至少需要 8 个字符，包含字母和数字";
    public static final String PASSWORD_NOT_SAME = "两次密码不一致";
    public static final String EMAIL_FORMAT_INCORRECT = "邮箱格式不正确";


    // 群聊相关错误
    public static final String GROUP_NOT_FOUND = "群聊未找到";
    public static final String USER_NOT_IN_GROUP = "用户不在该群聊中";
    public static final String GROUP_ALREADY_EXISTS = "群聊已存在";
    public static final String GROUP_LIMIT_REACHED = "群聊成员已达到上限，无法添加更多成员";
    public static final String GROUP_CREATION_FAILED = "群聊创建失败，请重试";

    // 消息相关错误
    public static final String MESSAGE_SEND_FAILED = "消息发送失败";
    public static final String MESSAGE_NOT_FOUND = "消息未找到";
    public static final String MESSAGE_TOO_LONG = "消息内容过长，最大支持 500 字符";
    public static final String INVALID_MESSAGE_FORMAT = "无效的消息格式，请检查输入";

    // 权限相关错误
    public static final String PERMISSION_DENIED = "权限被拒绝";
    public static final String ACTION_NOT_ALLOWED = "不允许的操作";
    public static final String GROUP_ADMIN_REQUIRED = "此操作需要群主权限";
    public static final String USER_BANNED = "用户已被禁言，无法发送消息";

    // 连接相关错误
    public static final String CONNECTION_TIMEOUT = "连接超时，请检查网络连接";
    public static final String SERVER_UNREACHABLE = "无法连接到服务器，请稍后重试";

    // 数据库相关错误
    public static final String DATABASE_ERROR = "数据库操作失败，请稍后重试";
    public static final String DATA_NOT_FOUND = "请求的数据未找到";

    // Redis相关错误
    public static final String REDIS_ERROR = "Redis操作失败，请稍后重试";

    // 通用错误
    public static final String UNKNOWN_ERROR = "未知错误，请稍后再试";
    public static final String OPERATION_FAILED = "操作失败，请重试";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
    public static final String SYSTEM_ERROR = "系统错误";


    public static final String INVALID_TOKEN = "提供的令牌无效或已过期";

    public static final String TOKEN_EXPIRED = "token过期，需要重新登录";
    public static final String TOKEN_GENERATION_FAILED = "token生成过程错误，请重新尝试或者联系管理员";
    public static final String INVALID_PASSWORD = "密码错误，请重新输入";
}
