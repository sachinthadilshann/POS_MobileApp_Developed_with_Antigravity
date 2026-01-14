package com.sachintha.posapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sachintha.posapp.database.entity.User;

import java.util.List;

/**
 * DAO for User entity operations
 */
@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(long id);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password AND isActive = 1")
    User authenticate(String username, String password);

    @Query("SELECT * FROM users WHERE isActive = 1")
    List<User> getAllActiveUsers();

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE role = :role AND isActive = 1")
    List<User> getUsersByRole(String role);
}
