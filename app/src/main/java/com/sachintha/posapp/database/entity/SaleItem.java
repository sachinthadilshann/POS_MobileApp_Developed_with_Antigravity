package com.sachintha.posapp.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * SaleItem entity for individual items in a sale
 */
@Entity(
    tableName = "sale_items",
    foreignKeys = {
        @ForeignKey(
            entity = Sale.class,
            parentColumns = "id",
            childColumns = "saleId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Product.class,
            parentColumns = "id",
            childColumns = "productId",
            onDelete = ForeignKey.SET_NULL
        )
    },
    indices = {@Index("saleId"), @Index("productId")}
)
public class SaleItem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long saleId;
    private Long productId;
    private String productName;
    private String productBarcode;
    private int quantity;
    private double unitPrice;
    private double discount;
    private double total;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSaleId() {
        return saleId;
    }

    public void setSaleId(long saleId) {
        this.saleId = saleId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
