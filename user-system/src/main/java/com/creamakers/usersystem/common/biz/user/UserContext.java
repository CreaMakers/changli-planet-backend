package com.creamakers.usersystem.common.biz.user;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

public final class UserContext {

    private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 设置用户至上下文
     *
     * @param user 用户详情信息
     */
    public static void setUser(UserInfoDTO user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 获取上下文中用户 ID
     *
     * @return 用户 ID
     */
    public static Integer getUserId() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get())
                .map(UserInfoDTO::getUserId)
                .orElse(null);
    }

    /**
     * 获取上下文中用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get())
                .map(UserInfoDTO::getUsername)
                .orElse(null);
    }

    /**
     * 获取上下文中用户 Token
     *
     * @return 用户 Token
     */
    public static String getToken() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get())
                .map(UserInfoDTO::getToken)
                .orElse(null);
    }

    /**
     * 获取上下文中用户是否为管理员
     *
     * @return 管理员权限 0-普通用户, 1-运营组, 2-开发组
     */
    public static Byte getIsAdmin() {
        return Optional.ofNullable(USER_THREAD_LOCAL.get())
                .map(UserInfoDTO::getIsAdmin)
                .orElse(null);
    }

    /**
     * 清理用户上下文
     */
    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }

    /**
     * 完全清除上下文，释放资源
     */
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }

    /**
     * 检查是否有用户信息在上下文中
     *
     * @return 如果上下文中有用户信息则返回 true，否则返回 false
     */
    public static boolean hasUser() {
        return USER_THREAD_LOCAL.get() != null;
    }
}
