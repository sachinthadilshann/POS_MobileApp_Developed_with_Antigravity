package com.sachintha.posapp.model;

import java.util.ArrayList;
import java.util.List;

import com.sachintha.posapp.database.entity.Product;

/**
 * Shopping Cart model for POS operations
 * Manages cart items and calculates totals
 */
public class Cart {

    private static Cart instance;
    private List<CartItem> items;
    private double discountPercentage;
    private double taxPercentage;

    private Cart() {
        items = new ArrayList<>();
        discountPercentage = 0;
        taxPercentage = 0; // Set to 0 by default, can be configured
    }

    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    /**
     * Add product to cart
     */
    public void addProduct(Product product) {
        // Check if product already exists in cart
        for (CartItem item : items) {
            if (item.getProductId() == product.getId()) {
                // Check if we have enough stock
                if (item.getQuantity() < product.getStock()) {
                    item.incrementQuantity();
                }
                return;
            }
        }
        // Add new item if not exists
        if (product.getStock() > 0) {
            items.add(new CartItem(product));
        }
    }

    /**
     * Add product with specific quantity
     */
    public void addProduct(Product product, int quantity) {
        for (CartItem item : items) {
            if (item.getProductId() == product.getId()) {
                int newQty = Math.min(item.getQuantity() + quantity, product.getStock());
                item.setQuantity(newQty);
                return;
            }
        }
        int qty = Math.min(quantity, product.getStock());
        if (qty > 0) {
            items.add(new CartItem(product, qty));
        }
    }

    /**
     * Remove product from cart
     */
    public void removeProduct(long productId) {
        items.removeIf(item -> item.getProductId() == productId);
    }

    /**
     * Update item quantity
     */
    public void updateQuantity(long productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProductId() == productId) {
                if (quantity <= 0) {
                    removeProduct(productId);
                } else {
                    int maxQty = item.getProduct().getStock();
                    item.setQuantity(Math.min(quantity, maxQty));
                }
                return;
            }
        }
    }

    /**
     * Increment item quantity
     */
    public void incrementQuantity(long productId) {
        for (CartItem item : items) {
            if (item.getProductId() == productId) {
                if (item.getQuantity() < item.getProduct().getStock()) {
                    item.incrementQuantity();
                }
                return;
            }
        }
    }

    /**
     * Decrement item quantity
     */
    public void decrementQuantity(long productId) {
        for (CartItem item : items) {
            if (item.getProductId() == productId) {
                if (item.getQuantity() > 1) {
                    item.decrementQuantity();
                } else {
                    removeProduct(productId);
                }
                return;
            }
        }
    }

    /**
     * Clear all items from cart
     */
    public void clear() {
        items.clear();
        discountPercentage = 0;
    }

    /**
     * Get all cart items
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * Get cart item count
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Get total quantity of all items
     */
    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Get subtotal (before discount and tax)
     */
    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal;
    }

    /**
     * Get discount amount
     */
    public double getDiscountAmount() {
        return getSubtotal() * (discountPercentage / 100);
    }

    /**
     * Get tax amount
     */
    public double getTaxAmount() {
        return (getSubtotal() - getDiscountAmount()) * (taxPercentage / 100);
    }

    /**
     * Get total amount
     */
    public double getTotal() {
        return getSubtotal() - getDiscountAmount() + getTaxAmount();
    }

    /**
     * Set discount percentage
     */
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    /**
     * Set tax percentage
     */
    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    /**
     * Check if product exists in cart
     */
    public boolean containsProduct(long productId) {
        for (CartItem item : items) {
            if (item.getProductId() == productId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get cart item by product ID
     */
    public CartItem getCartItem(long productId) {
        for (CartItem item : items) {
            if (item.getProductId() == productId) {
                return item;
            }
        }
        return null;
    }
}
