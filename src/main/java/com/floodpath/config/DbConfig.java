package com.floodpath.config;

public final class DbConfig {

    private DbConfig() {}

    public static final String HOST =
            System.getenv().getOrDefault("DB_HOST", "localhost");

    public static final String PORT =
            System.getenv().getOrDefault("DB_PORT", "3306");

    public static final String DATABASE =
            System.getenv().getOrDefault("DB_NAME", "floodpath");

    public static final String USER =
            System.getenv().getOrDefault("DB_USER", "root");

    public static final String PASSWORD =
            System.getenv().getOrDefault("DB_PASSWORD", "");

    public static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?sslMode=REQUIRED&serverTimezone=UTC";

    public static final String UPLOAD_DIR =
            System.getProperty("java.io.tmpdir") + "/floodpath-uploads";
}