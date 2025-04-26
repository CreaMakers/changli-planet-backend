package com.creamakers.usersystem.consts;

public class RedisKeyConst {
    // APK相关
    public static final String LATEST_APK_URL_KEY = "latest_apk_url";

    // 验证码有效期
    public static final int VERIFICATION_CODE_EXPIRE_MINUTES = 5; // 验证码有效期5分钟

    // 验证码前缀
    public static final String REGISTER_VERIFICATION_CODE_PREFIX = "CSUSTPLANT:EMAIL:REGISTER:VERIFICATION:";
    public static final String LOGIN_VERIFICATION_CODE_PREFIX = "CSUSTPLANT:EMAIL:LOGIN:VERIFICATION:";
    public static final String UPDATE_EMAIL_VERIFICATION_CODE_PREFIX = "CSUSTPLANT:EMAIL:UPDATE:VERIFICATION:";
    public static final String RESET_PASSWORD_VERIFICATION_CODE_PREFIX = "CSUSTPLANT:EMAIL:RESET:VERIFICATION:";

    // 限流相关
    public static final String EMAIL_THROTTLE_PREFIX = "CSUSTPLANT:EMAIL:THROTTLE:";

    // 每日限制相关
    public static final String REGISTER_DAILY_LIMIT_PREFIX = "CSUSTPLANT:EMAIL:REGISTER:DAILY:LIMIT:";
    public static final String LOGIN_DAILY_LIMIT_PREFIX = "CSUSTPLANT:EMAIL:LOGIN:DAILY:LIMIT:";
    public static final String UPDATE_EMAIL_DAILY_LIMIT_PREFIX = "CSUSTPLANT:EMAIL:UPDATE:DAILY:LIMIT:";
    public static final String RESET_PASSWORD_DAILY_LIMIT_PREFIX = "CSUSTPLANT:EMAIL:RESET:DAILY:LIMIT:";

    // 防止实例化
    private RedisKeyConst() {
        throw new IllegalStateException("Utility class");
    }

    public static String getKey(String prefix, String identifier) {
        return prefix + identifier;
    }

}