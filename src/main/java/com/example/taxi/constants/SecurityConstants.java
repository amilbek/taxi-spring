package com.example.taxi.constants;

public class SecurityConstants {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String AUTH_URLS = "/auth/**";
    public static final String ADMIN_URLS = "/admin/**";
    public static final String USERS_URLS = "/users/**";
    public static final String URLS = "/**";

    private SecurityConstants() {
    }
}
