package com.sachintha.posapp.model;

import com.sachintha.posapp.database.entity.Product;

/**
 * Cart Item model for POS operations
 * Represents a product in the shopping cart with quantity
 */
public class CartItem {

    private Product product;
    private int quantity;
    private double discount;

    public CartItem(Product product) {
        this.product = product;
        this.quantity = 1;
        this.discount = 0;
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.discount = 0;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    public double getTotal() {
        return getSubtotal() - discount;
    }

    public long getProductId() {
        return product.getId();
    }

    public String getProductName() {
        return product.getName();
    }

    public double getUnitPrice() {
        return product.getPrice();
    }

    public String getBarcode() {
        return product.getBarcode();
    }
}
