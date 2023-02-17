package com.itheima.reggie.common;

public class BaseContext {
    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    public static Long getCurrentId() {
        return THREAD_LOCAL.get();
    }

   public static void setCurrentId(Long id) {
        THREAD_LOCAL.set(id);
    }

    public static void removeCurrentId() {
        THREAD_LOCAL.remove();
    }
}
