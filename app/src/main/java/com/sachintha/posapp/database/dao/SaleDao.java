package com.sachintha.posapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sachintha.posapp.database.entity.Sale;

import java.util.Date;
import java.util.List;

/**
 * DAO for Sale entity operations
 */
@Dao
public interface SaleDao {

    @Insert
    long insert(Sale sale);

    @Update
    void update(Sale sale);

    @Delete
    void delete(Sale sale);

    @Query("SELECT * FROM sales WHERE id = :id")
    Sale getSaleById(long id);

    @Query("SELECT * FROM sales WHERE invoiceNumber = :invoiceNumber")
    Sale getSaleByInvoice(String invoiceNumber);

    @Query("SELECT * FROM sales ORDER BY saleDate DESC")
    List<Sale> getAllSales();

    @Query("SELECT * FROM sales WHERE status = :status ORDER BY saleDate DESC")
    List<Sale> getSalesByStatus(String status);

    @Query("SELECT * FROM sales WHERE saleDate BETWEEN :startDate AND :endDate ORDER BY saleDate DESC")
    List<Sale> getSalesBetweenDates(Date startDate, Date endDate);

    @Query("SELECT * FROM sales WHERE userId = :userId ORDER BY saleDate DESC")
    List<Sale> getSalesByUser(long userId);

    @Query("SELECT COALESCE(SUM(total), 0) FROM sales WHERE status = 'COMPLETED'")
    double getTotalSales();

    @Query("SELECT COALESCE(SUM(total), 0) FROM sales WHERE saleDate BETWEEN :startDate AND :endDate AND status = 'COMPLETED'")
    double getTotalSalesBetweenDates(Date startDate, Date endDate);

    @Query("SELECT COUNT(*) FROM sales WHERE status = 'COMPLETED'")
    int getTotalSalesCount();

    @Query("SELECT COUNT(*) FROM sales WHERE saleDate BETWEEN :startDate AND :endDate AND status = 'COMPLETED'")
    int getSalesCountBetweenDates(Date startDate, Date endDate);

    @Query("SELECT * FROM sales ORDER BY id DESC LIMIT 1")
    Sale getLastSale();

    @Query("SELECT * FROM sales WHERE DATE(saleDate/1000, 'unixepoch', 'localtime') = DATE('now', 'localtime') AND status = 'COMPLETED' ORDER BY saleDate DESC")
    List<Sale> getTodaySales();

    @Query("SELECT COALESCE(SUM(total), 0) FROM sales WHERE DATE(saleDate/1000, 'unixepoch', 'localtime') = DATE('now', 'localtime') AND status = 'COMPLETED'")
    double getTodayTotal();
}
