package com.sachintha.posapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sachintha.posapp.database.entity.SaleItem;

import java.util.List;

/**
 * DAO for SaleItem entity operations
 */
@Dao
public interface SaleItemDao {

    @Insert
    long insert(SaleItem saleItem);

    @Insert
    void insertAll(List<SaleItem> saleItems);

    @Update
    void update(SaleItem saleItem);

    @Delete
    void delete(SaleItem saleItem);

    @Query("SELECT * FROM sale_items WHERE id = :id")
    SaleItem getSaleItemById(long id);

    @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
    List<SaleItem> getSaleItemsBySaleId(long saleId);

    @Query("DELETE FROM sale_items WHERE saleId = :saleId")
    void deleteBySaleId(long saleId);

    @Query("SELECT SUM(quantity) FROM sale_items WHERE productId = :productId")
    int getTotalQuantitySold(long productId);
}
