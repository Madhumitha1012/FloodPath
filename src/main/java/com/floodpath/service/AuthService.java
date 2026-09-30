package com.floodpath.service;

import com.floodpath.config.DbConfig;
import com.floodpath.model.User;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public final class AuthService {
    private static final AuthService INSTANCE = new AuthService();
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicInteger memoryId = new AtomicInteger(100_000);

    private static final class Account {
        final User user;
        final String password;
        Account(User user, String password) {
            this.user = user;
            this.password = password;
        }
    }

    private AuthService() {
        loadUsersFromDb();
    }

    public static AuthService getInstance() {
        return INSTANCE;
    }

    public static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private Connection connection() throws SQLException {
        return DriverManager.getConnection(DbConfig.URL, DbConfig.USER, DbConfig.PASSWORD);
    }

    private void loadUsersFromDb() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection c = connection();
                 PreparedStatement ps = c.prepareStatement(
                     "SELECT id,name,email,role,password FROM users WHERE password IS NOT NULL")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String email = normalize(rs.getString("email"));
                        String role = rs.getString("role");
                        User u = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            email,
                            role,
                            true
                        );
                        accounts.put(email, new Account(u, rs.getString("password")));
                    }
                }
            }
        } catch (Exception ignored) {
            // The application can still register/login in memory during demo mode.
        }
    }

    public User register(String name, String email, String password) {
        name = name == null ? "" : name.trim();
        email = normalize(email);

        if (name.length() < 2 || name.length() > 100)
            throw new IllegalArgumentException("Please enter your name (2-100 characters).");
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || email.length() > 150)
            throw new IllegalArgumentException("Please enter a valid email address.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");

        synchronized (this) {
            if (accounts.containsKey(email))
                throw new IllegalArgumentException("An account with this email already exists.");

            int id = memoryId.incrementAndGet();
            boolean persisted = false;

            try (Connection c = connection();
                 PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users(name,email,role,password) VALUES(?,?,'USER',?)",
                     Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        id = keys.getInt(1);
                        persisted = true;
                    }
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                throw new IllegalArgumentException("An account with this email already exists.");
            } catch (Exception ignored) {
                // Keep the user in memory for demo mode if MySQL is unavailable.
            }

            User u = new User(id, name, email, "USER", persisted);
            accounts.put(email, new Account(u, password));
            return u;
        }
    }

    public User login(String email, String password) {
        email = normalize(email);
        if (password == null) return null;

        Account a = accounts.get(email);

      
        if (a == null) {
            a = loadAccount(email);
            if (a != null) accounts.put(email, a);
        }

        if (a == null) return null;
        if (!password.equals(a.password)) return null;

        return a.user;
    }

    private Account loadAccount(String email) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection c = connection();
                 PreparedStatement ps = c.prepareStatement(
                     "SELECT id,name,email,role,password FROM users WHERE LOWER(email)=LOWER(?) LIMIT 1")) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User u = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            normalize(rs.getString("email")),
                            rs.getString("role"),
                            true
                        );
                        return new Account(u, rs.getString("password"));
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public List<User> listUsers() {
        List<User> list = new ArrayList<>();
        for (Account a : accounts.values()) list.add(a.user);
        list.sort(Comparator.comparingInt(User::getId));
        return list;
    }
}
