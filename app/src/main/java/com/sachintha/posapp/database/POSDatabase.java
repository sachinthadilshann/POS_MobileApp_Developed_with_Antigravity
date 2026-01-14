package com.sachintha.posapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.sachintha.posapp.database.dao.*;
import com.sachintha.posapp.database.entity.*;

/**
 * Room Database for POS Application
 * Contains all entities and DAOs
 */
@Database(
    entities = {
        User.class,
        Category.class,
        Product.class,
        Sale.class,
        SaleItem.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter.class)
public abstract class POSDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "pos_database";
    private static volatile POSDatabase instance;

    // DAOs
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract SaleDao saleDao();
    public abstract SaleItemDao saleItemDao();

    public static POSDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (POSDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            POSDatabase.class,
                            DATABASE_NAME
                    )
                    .allowMainThreadQueries() // For simplicity; use background threads in production
                    .fallbackToDestructiveMigration()
                    .build();
                    
                    // Initialize default data
                    initializeDefaultData(instance);
                }
            }
        }
        return instance;
    }

    private static void initializeDefaultData(POSDatabase db) {
        // Check if admin user exists
        if (db.userDao().getUserByUsername("admin") == null) {
            // Create default admin user
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // In production, use proper hashing
            admin.setFullName("Administrator");
            admin.setRole("ADMIN");
            admin.setActive(true);
            db.userDao().insert(admin);

            // Create default cashier
            User cashier = new User();
            cashier.setUsername("cashier");
            cashier.setPassword("cashier123");
            cashier.setFullName("Default Cashier");
            cashier.setRole("CASHIER");
            cashier.setActive(true);
            db.userDao().insert(cashier);
        }

        // Check if categories exist
        if (db.categoryDao().getAllCategories().isEmpty()) {
            String[] categories = {"Groceries", "Beverages", "Dairy", "Snacks", "Frozen Foods", 
                                   "Personal Care", "Household", "Bakery", "Fruits & Vegetables", "Meat & Seafood"};
            for (String cat : categories) {
                Category category = new Category();
                category.setName(cat);
                category.setActive(true);
                db.categoryDao().insert(category);
            }
        }

        // Add sample products if none exist
        if (db.productDao().getAllProducts().isEmpty()) {
            addSampleProducts(db);
        }
    }

    private static void addSampleProducts(POSDatabase db) {
        // Get category IDs
        Category groceries = db.categoryDao().getCategoryByName("Groceries");
        Category beverages = db.categoryDao().getCategoryByName("Beverages");
        Category dairy = db.categoryDao().getCategoryByName("Dairy");
        Category snacks = db.categoryDao().getCategoryByName("Snacks");

        if (groceries != null) {
            addProduct(db, "Rice (5kg)", "8901234567890", 450.00, 425.00, 50, groceries.getId());
            addProduct(db, "Wheat Flour (1kg)", "8901234567891", 45.00, 40.00, 100, groceries.getId());
            addProduct(db, "Sugar (1kg)", "8901234567892", 48.00, 42.00, 80, groceries.getId());
            addProduct(db, "Salt (1kg)", "8901234567893", 20.00, 15.00, 120, groceries.getId());
            addProduct(db, "Cooking Oil (1L)", "8901234567894", 180.00, 165.00, 45, groceries.getId());
        }

        if (beverages != null) {
            addProduct(db, "Coca Cola (500ml)", "8901234567895", 40.00, 35.00, 200, beverages.getId());
            addProduct(db, "Pepsi (500ml)", "8901234567896", 40.00, 35.00, 180, beverages.getId());
            addProduct(db, "Mineral Water (1L)", "8901234567897", 20.00, 15.00, 300, beverages.getId());
            addProduct(db, "Orange Juice (1L)", "8901234567898", 120.00, 100.00, 60, beverages.getId());
            addProduct(db, "Green Tea (25 bags)", "8901234567899", 150.00, 130.00, 40, beverages.getId());
        }

        if (dairy != null) {
            addProduct(db, "Fresh Milk (1L)", "8901234567900", 65.00, 58.00, 50, dairy.getId());
            addProduct(db, "Butter (500g)", "8901234567901", 280.00, 250.00, 30, dairy.getId());
            addProduct(db, "Cheese (200g)", "8901234567902", 180.00, 160.00, 25, dairy.getId());
            addProduct(db, "Yogurt (400g)", "8901234567903", 45.00, 38.00, 60, dairy.getId());
        }

        if (snacks != null) {
            addProduct(db, "Potato Chips (100g)", "8901234567904", 30.00, 25.00, 150, snacks.getId());
            addProduct(db, "Chocolate Bar", "8901234567905", 50.00, 42.00, 100, snacks.getId());
            addProduct(db, "Cookies (250g)", "8901234567906", 85.00, 75.00, 80, snacks.getId());
            addProduct(db, "Nuts Mix (200g)", "8901234567907", 220.00, 195.00, 40, snacks.getId());
        }
    }

    private static void addProduct(POSDatabase db, String name, String barcode, 
                                   double price, double cost, int stock, long categoryId) {
        Product product = new Product();
        product.setName(name);
        product.setBarcode(barcode);
        product.setPrice(price);
        product.setCostPrice(cost);
        product.setStock(stock);
        product.setCategoryId(categoryId);
        product.setActive(true);
        db.productDao().insert(product);
    }
}
