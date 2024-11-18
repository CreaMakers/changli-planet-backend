package com.creamakers.websystem.context;

public class UserContext {

    private static ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> userNameThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<String> tokenThreadLocal = new ThreadLocal<>();

    // 设置用户信息
    public static void set(Long userId, String userName, String token) {
        userIdThreadLocal.set(userId);
        userNameThreadLocal.set(userName);
        tokenThreadLocal.set(token);
    }

    // 获取用户信息
    public static Long getUserId() {
        return userIdThreadLocal.get();
    }

    public static String getUserName() {
        return userNameThreadLocal.get();
    }

    public static String getToken() {
        return tokenThreadLocal.get();
    }

    // 清除上下文
    public static void clear() {
        userIdThreadLocal.remove();
        userNameThreadLocal.remove();
        tokenThreadLocal.remove();
    }
}

