package com.sachintha.posapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sachintha.posapp.database.entity.Product;

import java.util.List;

/**
 * DAO for Product entity operations
 */
@Dao
public interface ProductDao {

    @Insert
    long insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products WHERE id = :id")
    Product getProductById(long id);

    @Query("SELECT * FROM products WHERE barcode = :barcode AND isActive = 1")
    Product getProductByBarcode(String barcode);

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name")
    List<Product> getAllActiveProducts();

    @Query("SELECT * FROM products ORDER BY name")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isActive = 1 ORDER BY name")
    List<Product> getProductsByCategory(long categoryId);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%' AND isActive = 1")
    List<Product> searchProducts(String query);

    @Query("SELECT * FROM products WHERE stock <= minStock AND isActive = 1")
    List<Product> getLowStockProducts();

    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId")
    void decreaseStock(long productId, int quantity);

    @Query("UPDATE products SET stock = stock + :quantity WHERE id = :productId")
    void increaseStock(long productId, int quantity);

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1")
    int getActiveProductCount();

    @Query("SELECT COUNT(*) FROM products WHERE stock <= minStock AND isActive = 1")
    int getLowStockCount();
}
