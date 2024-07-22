package com.juzipi.interceptor;

public class RequestContext {
    private static final ThreadLocal<String> userAccountHolder = new ThreadLocal<>();

    public static void setUserAccount(String userAccount) {
        userAccountHolder.set(userAccount);
    }

    public static String getUserAccount() {
        return userAccountHolder.get();
    }

    public static void clear() {
        userAccountHolder.remove();
    }
}
