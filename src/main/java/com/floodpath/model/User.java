package com.floodpath.model;

public class User {
    private final int id;
    private final String name;
    private final String email;
    private final String role; // USER or ADMIN
    private final boolean persisted;

    public User(int id, String name, String email, String role, boolean persisted) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.persisted = persisted;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isPersisted() { return persisted; }
    public boolean isAdmin() { return "ADMIN".equals(role); }
}
