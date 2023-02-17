package com.itheima.reggie.common;

public class BaseContext {
    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(String value) {
        THREAD_LOCAL.set(value);
    }

    public static String get() {
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
