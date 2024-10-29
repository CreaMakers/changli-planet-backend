package com.creamakers.websystem.context;

public class TokenContext {
    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void set(String token) {
        threadLocal.set(token);
    }

    public static String getCurrentToken() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
