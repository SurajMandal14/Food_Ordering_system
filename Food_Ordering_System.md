# Food Ordering System - Code Overview and Explanation

## Project Structure

This Java project implements a food ordering system with a clear separation of:
- **Database layer**: Using SQLite through JDBC connection
- **Business logic layer**: Java classes representing entities and operations
- **Presentation layer**: Swing-based GUI windows for different user roles

### Core Java Classes and Their Connections

#### Entry Point
- **Main.java**: Application entry point that initializes UI and sets system look and feel
  ```java
  // Main.java (line 12-17)
  SwingUtilities.invokeLater(() -> {
      try {
          MainWindow mainWindow = new MainWindow();
          mainWindow.setVisible(true);
      } catch (Exception e) { /* error handling */ }
  });
  ```

#### Database Connection
- **DatabaseConnection.java**: Singleton class managing database connectivity
  ```java
  // Key method in DatabaseConnection.java (line 11-29)
  public static synchronized Connection getConnection() {
      if (connection == null || connection.isClosed()) {
          connection = DriverManager.getConnection(DB_URL);
          // Initialize tables if needed
      }
      return connection;
  }
  ```
  - All other classes use this method to get database connections
  - The class automatically creates tables on first run (lines 31-173)
  - Uses transaction management for critical operations

#### User Authentication
- **User.java**: Handles user data and authentication
  ```java
  // Authentication method (line 31-52)
  public static User authenticate(String username, String password) {
      String sql = "SELECT id, username, role, full_name, ... FROM users WHERE username = ? AND password = ?";
      // Database connection and SQL execution
      // Returns User object if authenticated, null otherwise
  }
  ```
  - Used by **MainWindow.java**, **LoginWindow.java** for authentication
  - Connected to **AdminWindow.java** and **CustomerWindow.java** which receive the authenticated User object

#### Menu Item Management
- **MenuItem.java**: Food item representation and operations
  ```java
  // Method to get all menu items (line 53-78)
  public static List<MenuItem> getAllItems() {
      List<MenuItem> items = new ArrayList<>();
      String sql = "SELECT * FROM menu WHERE is_available = 1 ORDER BY category, item_name";
      // Database connection and SQL execution
      // Returns list of all available menu items
  }
  ```
  - Used by **AdminWindow.java** (line 334-357) to display and manage menu items
  - Used by **CustomerWindow.java** (line 389-404) to display menu for customers

#### Shopping Cart
- **Cart.java**: Manages user's shopping cart in database
  ```java
  // Adding item to cart (line 8-41)
  public static boolean addToCart(int userId, int itemId, int quantity) {
      // Check if item exists in cart
      // Update quantity if exists, insert new record if not
  }
  ```
  - Connected to **CustomerWindow.java** through cart operations (line 405-424)
  - Works with **CartItem.java** to represent items in the cart

#### Order Processing
- **Order.java**: Handles order creation and management
  ```java
  // Creating an order from cart items (line 172-239)
  public static boolean createOrder(int userId) {
      // Start transaction
      // Get cart items
      // Create order record
      // Transfer cart items to order_items
      // Clear cart
      // Commit transaction
  }
  ```
  - Used by **CustomerWindow.java** for placing orders (line 470-504)
  - Used by **AdminWindow.java** for managing orders (line 374-389)

## Workflow Implementation Details

### 1. User Authentication Flow
```
Main.java → MainWindow.java → [LoginWindow.java OR RegisterWindow.java] → [CustomerWindow.java OR AdminWindow.java]
```

1. **MainWindow.java** displays the login screen with username/password fields
   ```java
   // MainWindow.java (line 193-211)
   private void handleLogin() {
       String username = usernameField.getText();
       String password = new String(passwordField.getPassword());
       
       User user = User.authenticate(username, password);
       if (user != null) {
           if (adminModeCheckBox.isSelected() && user.getRole().equals("Admin")) {
               new AdminWindow(user).setVisible(true);
           } else {
               new CustomerWindow(user).setVisible(true);
           }
           this.dispose();
       }
   }
   ```

2. User can register through **RegisterWindow.java** which calls:
   ```java
   // User.java (line 55-74) 
   public static boolean register(String username, String password, String fullName, String email, String phone, String address) {
       String sql = "INSERT INTO users (username, password, role, full_name, email, phone, address) VALUES (?, ?, 'Customer', ?, ?, ?, ?)";
       // Execute SQL to create new user
   }
   ```

### 2. Admin Workflow Implementation
```
AdminWindow.java → MenuItem.java/Order.java → DatabaseConnection.java → Database
```

1. **AdminWindow.java** has two main tabs in the UI for:
   - Menu management (line 117-252): Add, edit, delete menu items
   - Order management (line 253-333): View and update order status

2. Menu operations call methods in **MenuItem.java**:
   ```java
   // AdminWindow.java (line 636-660)
   private void deleteSelectedItem() {
       int row = menuTable.getSelectedRow();
       if (row >= 0) {
           int id = (int) menuTable.getValueAt(row, 0);
           if (MenuItem.deleteItem(id)) {
               loadMenuItems(); // Refresh the table
           }
       }
   }
   ```

3. Order handling uses methods from **Order.java**:
   ```java
   // AdminWindow.java (line 661-701)
   private void updateOrderStatus() {
       int row = ordersTable.getSelectedRow();
       if (row >= 0) {
           int orderId = (int) ordersTable.getValueAt(row, 0);
           String[] statuses = {"Pending", "Preparing", "Out for Delivery", "Delivered", "Cancelled"};
           String selected = (String) JOptionPane.showInputDialog(
               this, "Select new status:", "Update Order Status",
               JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);
           
           if (selected != null) {
               Order.updateOrderStatus(orderId, selected);
               loadOrders(); // Refresh the orders table
           }
       }
   }
   ```

### 3. Customer Workflow Implementation
```
CustomerWindow.java → MenuItem.java → Cart.java → Order.java → DatabaseConnection.java → Database
```

1. **CustomerWindow.java** has three main tabs:
   - Menu browsing (line 130-240): View and add items to cart
   - Cart management (line 241-328): View, update, remove items, checkout
   - Order history (line 329-388): View order status and history

2. Adding items to cart:
   ```java
   // CustomerWindow.java (line 405-424)
   private void addToCart(int quantity) {
       int row = menuTable.getSelectedRow();
       if (row >= 0) {
           int itemId = (int) menuTable.getValueAt(row, 0);
           Cart.addToCart(currentUser.getId(), itemId, quantity);
           JOptionPane.showMessageDialog(this, "Item added to cart!");
       }
   }
   ```

3. Checkout process:
   ```java
   // CustomerWindow.java (line 470-504)
   private void checkout() {
       // Get cart total
       double total = Cart.calculateCartTotal(currentUser.getId());
       
       // Get delivery information
       String address = JOptionPane.showInputDialog(this, "Delivery Address:", currentUser.getAddress());
       if (address != null) {
           // Create order from cart items
           if (Order.createOrder(currentUser.getId())) {
               JOptionPane.showMessageDialog(this, "Order placed successfully!");
               updateCart(); // Clear the cart view
           }
       }
   }
   ```

## Key Functions for Viva Explanation

1. **Database Connection Management** (DatabaseConnection.java):
   - Explain the singleton pattern implementation (line 11-29)
   - Point out transaction management for critical operations (line 31-173)
   - Discuss automatic table creation and schema upgrades (line 73-84)

2. **User Authentication** (User.java):
   - Explain how authentication works (line 31-52)
   - Show how passwords are verified (could be improved with hashing)
   - Discuss role-based access control (Admin vs Customer)

3. **Menu Item Management** (MenuItem.java):
   - Explain CRUD operations for menu items (lines 53-270)
   - Point out image handling for food items (line 283-326)
   - Discuss categorization of menu items

4. **Shopping Cart Implementation** (Cart.java):
   - Explain how items are added/updated in cart (line 8-41)
   - Show cart total calculation (line 124-151)
   - Discuss the relationship between Cart and CartItem classes

5. **Order Processing** (Order.java):
   - Explain the complete order creation process (line 172-239)
   - Show order status management (line 444-468)
   - Highlight transaction management for order creation

6. **UI Implementation** (AdminWindow.java, CustomerWindow.java):
   - Explain the tabbed interface design
   - Discuss event handling for user interactions
   - Show how UI elements are updated based on database changes
   - Point out the timer-based refresh for orders (line 390-398)

## Database Schema Details

### users table
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

### menu table
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

### orders and order_items tables
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