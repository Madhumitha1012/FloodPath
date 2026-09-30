package com.floodpath.config;

public final class DbConfig {
    private DbConfig() {}

    public static final String URL =
            "jdbc:mysql://localhost:3306/floodpath?useSSL=false&serverTimezone=UTC";
    public static final String USER = "root";
    public static final String PASSWORD = "madhu1012";

    public static final String ADMIN_NAME = "FloodPath Admin";
    public static final String ADMIN_EMAIL = env("FLOODPATH_ADMIN_EMAIL", "admin@floodpath.local");
    public static final String ADMIN_PASSWORD = env("FLOODPATH_ADMIN_PASSWORD", "Admin@123");

  
    public static final String UPLOAD_DIR = env("FLOODPATH_UPLOAD_DIR",
            System.getProperty("user.home") + java.io.File.separator + "floodpath-uploads");

    private static String env(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }
}
