# SuperPOS - Supermarket Point of Sale Application

A comprehensive **Java-based Android POS application** for supermarkets with modern Material Design UI.

## 🛒 Features

### Core Features
- **User Authentication** - Login with role-based access (Admin/Cashier)
- **Dashboard** - Real-time sales statistics and quick actions
- **POS Terminal** - Fast checkout with product grid and cart management
- **Barcode Scanning** - ML Kit powered barcode scanning for quick product lookup
- **Product Management** - Full CRUD operations for products
- **Inventory Management** - Stock tracking with low stock alerts
- **Sales History** - Complete transaction history with detailed receipts
- **Receipt Generation** - PDF receipt generation for printing

### Technical Features
- **Room Database** - Local SQLite database with type-safe queries
- **CameraX** - Modern camera API for barcode scanning
- **ML Kit** - Google's machine learning for barcode detection
- **Material Design** - Modern dark theme UI with smooth animations
- **View Binding** - Type-safe view access

## 📱 Screenshots

The app features a modern dark theme with:
- Animated splash screen
- Card-based dashboard
- Split-panel POS terminal
- Grid product display
- Sliding cart panel

## 🔐 Default Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Cashier | cashier | cashier123 |

## 🏗️ Project Structure

```
app/src/main/java/com/sachintha/posapp/
├── POSApplication.java          # Application class
├── database/
│   ├── POSDatabase.java         # Room database
│   ├── DateConverter.java       # Type converter
│   ├── dao/                     # Data Access Objects
│   │   ├── UserDao.java
│   │   ├── CategoryDao.java
│   │   ├── ProductDao.java
│   │   ├── SaleDao.java
│   │   └── SaleItemDao.java
│   └── entity/                  # Database entities
│       ├── User.java
│       ├── Category.java
│       ├── Product.java
│       ├── Sale.java
│       └── SaleItem.java
├── model/
│   ├── Cart.java                # Shopping cart singleton
│   └── CartItem.java            # Cart item model
├── utils/
│   ├── SessionManager.java      # User session handling
│   └── FormatUtils.java         # Formatting utilities
├── adapter/
│   ├── ProductGridAdapter.java  # POS product grid
│   ├── ProductListAdapter.java  # Product list
│   ├── CartAdapter.java         # Shopping cart
│   ├── SalesAdapter.java        # Sales history
│   ├── SaleItemsAdapter.java    # Receipt items
│   └── InventoryAdapter.java    # Inventory list
└── ui/
    ├── SplashActivity.java      # Splash screen
    ├── LoginActivity.java       # Authentication
    ├── MainActivity.java        # Dashboard
    ├── POSActivity.java         # POS terminal
    ├── ProductActivity.java     # Product list
    ├── AddEditProductActivity.java  # Product form
    ├── SalesHistoryActivity.java    # Sales list
    ├── SaleDetailsActivity.java     # Receipt view
    ├── BarcodeScannerActivity.java  # Barcode scanner
    ├── InventoryActivity.java       # Stock management
    └── SettingsActivity.java        # Admin settings
```

## 🛠️ Building the App

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK 34

### Build Steps

1. **Open the project** in Android Studio
2. **Sync Gradle** files (File > Sync Project with Gradle Files)
3. **Build the project** (Build > Make Project)
4. **Run on device/emulator** (Run > Run 'app')

### Build APK
```bash
./gradlew assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

## 📦 Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Room | 2.6.1 | Local database |
| CameraX | 1.3.1 | Camera preview |
| ML Kit Barcode | 17.2.0 | Barcode scanning |
| Material Components | 1.11.0 | UI components |
| Gson | 2.10.1 | JSON parsing |

## 🎨 UI Theme

The app uses a custom dark theme with:
- **Primary**: #6366F1 (Indigo)
- **Background**: #0F172A (Dark Navy)
- **Success**: #10B981 (Green)
- **Warning**: #F59E0B (Amber)
- **Error**: #EF4444 (Red)

## 📋 Database Schema

### Users
- id, username, password, fullName, role, isActive

### Categories
- id, name, description, isActive

### Products
- id, name, barcode, description, price, costPrice, stock, minStock, categoryId, isActive

### Sales
- id, invoiceNumber, userId, cashierName, saleDate, subtotal, discount, tax, total, amountPaid, change, paymentMethod, status

### SaleItems
- id, saleId, productId, productName, productBarcode, quantity, unitPrice, discount, total

## 🔧 Future Enhancements

- [ ] Cloud sync with Firebase
- [ ] Multi-store support
- [ ] Customer management
- [ ] Loyalty program
- [ ] Reports and analytics
- [ ] Bluetooth printer support
- [ ] Offline-first architecture

## 📄 License

This project is for educational purposes.

---

**Built with ❤️ using Java and Android**
