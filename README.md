# Food Ordering System

A Java-based desktop application for food ordering with separate customer and admin interfaces.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Database Structure](#database-structure)
- [Class Structure](#class-structure)
- [Installation & Setup](#installation--setup)
- [Dependencies](#dependencies)
- [Usage](#usage)
- [Technical Details](#technical-details)

## Overview

This Food Ordering System is a standalone Java Swing application that allows customers to browse menu items, add them to a cart, and place orders. Administrators can manage menu items and track/update orders. The system uses SQLite for data persistence.

## Features

### Customer Features

- Browse menu items with images, descriptions, and prices
- Add items to cart with quantity selection
- View and manage shopping cart (remove items)
- Place orders
- View order history and status
- Automatic order status updates

### Admin Features

- Manage menu items (add, edit, delete)
- View and update order statuses
- Track all customer orders
- Switch between admin and customer modes (for admin users)

## System Architecture

The system follows a basic MVC (Model-View-Controller) architecture:

- **Model**: Java classes representing data entities (User, MenuItem, Order, etc.)
- **View**: Swing-based UI components (MainWindow, CustomerWindow, AdminWindow, etc.)
- **Controller**: Database interaction and business logic (DatabaseConnection class and static methods in entity classes)

## Database Structure

The application uses SQLite (file-based database) stored in `food_ordering.db` in the project root directory. Database schema includes:

### Tables:

1. **users** - Stores user account information

   ```sql
   CREATE TABLE IF NOT EXISTS users (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       username TEXT NOT NULL UNIQUE,
       password TEXT NOT NULL,
       role TEXT NOT NULL,
       full_name TEXT,
       email TEXT,
       phone TEXT,
       address TEXT,
       join_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   )
   ```

2. **menu** - Stores food menu items

   ```sql
   CREATE TABLE IF NOT EXISTS menu (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       item_name TEXT NOT NULL,
       description TEXT,
       price REAL NOT NULL,
       category TEXT,
       image_path TEXT,
       is_vegetarian INTEGER DEFAULT 0,
       is_spicy INTEGER DEFAULT 0,
       calories INTEGER,
       preparation_time INTEGER,
       is_available INTEGER DEFAULT 1
   )
   ```

3. **orders** - Stores customer orders

   ```sql
   CREATE TABLE IF NOT EXISTS orders (
       order_id INTEGER PRIMARY KEY AUTOINCREMENT,
       user_id INTEGER NOT NULL,
       order_status TEXT NOT NULL,
       order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       delivery_address TEXT,
       special_instructions TEXT,
       payment_method TEXT DEFAULT 'Cash',
       payment_status TEXT DEFAULT 'Pending',
       total_amount REAL DEFAULT 0,
       estimated_delivery_time TIMESTAMP,
       actual_delivery_time TIMESTAMP,
       rating INTEGER,
       feedback TEXT,
       FOREIGN KEY (user_id) REFERENCES users(id)
   )
   ```

4. **order_items** - Stores items in each order

   ```sql
   CREATE TABLE IF NOT EXISTS order_items (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       order_id INTEGER NOT NULL,
       item_id INTEGER NOT NULL,
       quantity INTEGER NOT NULL,
       price REAL NOT NULL,
       FOREIGN KEY (order_id) REFERENCES orders(order_id),
       FOREIGN KEY (item_id) REFERENCES menu(id)
   )
   ```

5. **cart** - Stores user's shopping cart

   ```sql
   CREATE TABLE IF NOT EXISTS cart (
       id INTEGER PRIMARY KEY AUTOINCREMENT,
       user_id INTEGER NOT NULL,
       item_id INTEGER NOT NULL,
       quantity INTEGER NOT NULL,
       special_instructions TEXT,
       FOREIGN KEY (user_id) REFERENCES users(id),
       FOREIGN KEY (item_id) REFERENCES menu(id)
   )
   ```

6. Additional tables: **categories**, **favorites**, **promotions**

## Class Structure

### Main Components:

- **Main.java**: Application entry point
- **MainWindow.java**: Central interface with login functionality and mode selection
- **LoginWindow.java**: User authentication interface
- **RegisterWindow.java**: New user registration interface
- **CustomerWindow.java**: Customer interface for browsing and ordering
- **AdminWindow.java**: Admin interface for menu and order management
- **DatabaseConnection.java**: Manages database connections and schema

### Data Models:

- **User.java**: User account data and authentication
- **MenuItem.java**: Food item data and CRUD operations
- **Order.java**: Order processing and management
- **CartItem.java**: Shopping cart item representation
- **Cart.java**: Shopping cart functionality
- **ImageHandler.java**: Image processing for menu items

## Installation & Setup

### Prerequisites

- Java JDK 11 or higher
- SQLite (included as JDBC driver)

### Installation Steps

1. Clone the repository
   ```
   git clone https://github.com/yourusername/food-ordering-system.git
   ```
2. Navigate to the project directory
   ```
   cd food-ordering-system
   ```
3. Compile the Java files
   ```
   javac -cp "sqlite-jdbc-3.49.1.0.jar" src/*.java
   ```
4. Run the application
   ```
   java -cp ".;sqlite-jdbc-3.49.1.0.jar" src.Main
   ```
   (Use `:` instead of `;` on Linux/Mac)

## Dependencies

- **SQLite JDBC Driver** (sqlite-jdbc-3.49.1.0.jar): JDBC driver for SQLite database connectivity

## Usage

### Default Admin Account

- Username: `admin`
- Password: `admin123`

### Customer Account

- Create a new account using the Registration form or use an existing one.

## Technical Details

### Database Connection

The application uses a singleton pattern for database connection management:

- The `DatabaseConnection` class manages a single connection instance
- Connection is established to the SQLite file database "food_ordering.db"
- Schema creation and validation occurs on first connection
- Automatic schema upgrade mechanism for handling schema changes

### Image Handling

- Menu item images are stored in the `product_images/` directory
- The `ImageHandler` class manages image loading, saving, and resizing
- Images are displayed in UI components using Java Swing's `ImageIcon` class

### Transaction Management

For critical operations like order creation, the system uses transaction management:

- Connection auto-commit is disabled during transaction
- Operations are rolled back on failure
- Resources are properly closed in finally blocks

### Security Considerations

- Passwords are stored in plain text (in a production system, these should be hashed)
- Connection parameters are hardcoded (should be externalized in configuration for production)

### UI Design

- Java Swing components with custom styling
- Responsive grid layouts for different screen sections
- Custom table cell renderers for displaying images
- Tab-based navigation for customer interface
- Modal dialogs for actions requiring confirmation

### Data Flow

1. User authentication in MainWindow/LoginWindow
2. Menu items loaded from database via MenuItem class
3. Cart operations managed by OrderItem inner class
4. Orders created and persisted via Order class
5. Admin operations update database directly through their respective classes

### Future Improvements

- Implement password hashing for security
- Add payment processing functionality
- Implement customer profiles with saved addresses
- Add reporting functionality for admins
- Implement order tracking with real-time updates
