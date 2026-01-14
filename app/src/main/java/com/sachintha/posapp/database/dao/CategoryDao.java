package com.sachintha.posapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sachintha.posapp.database.entity.Category;

import java.util.List;

/**
 * DAO for Category entity operations
 */
@Dao
public interface CategoryDao {

    @Insert
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(long id);

    @Query("SELECT * FROM categories WHERE name = :name")
    Category getCategoryByName(String name);

    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY name")
    List<Category> getAllActiveCategories();

    @Query("SELECT * FROM categories ORDER BY name")
    List<Category> getAllCategories();

    @Query("SELECT COUNT(*) FROM products WHERE categoryId = :categoryId")
    int getProductCount(long categoryId);
}
