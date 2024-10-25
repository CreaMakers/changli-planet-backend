package com.creamakers.websystem.context;

public class UserNameContext {
    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void set(String username) {
        threadLocal.set(username);
    }

    public static String getCurrentName() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
