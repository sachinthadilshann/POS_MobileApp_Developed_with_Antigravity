package com.sachintha.posapp.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Category entity for product categorization
 */
@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;
    private String description;
    private boolean isActive;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return name;
    }
}
