package com.sachintha.posapp.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * User entity for authentication and role management
 */
@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String username;
    private String password;
    private String fullName;
    private String role; // ADMIN, CASHIER
    private boolean isActive;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
