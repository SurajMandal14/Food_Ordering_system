package src;

import java.sql.*;
import java.io.File;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:food_ordering.db";
    private static Connection connection = null;
    private static boolean initializing = false;
    private static boolean tablesCreated = false;

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Prevent recursive calls during initialization
                if (initializing) {
                    return DriverManager.getConnection(DB_URL);
                }
                
                initializing = true;
                connection = DriverManager.getConnection(DB_URL);
                connection.setAutoCommit(true);
                
                // Only create tables once
                if (!tablesCreated) {
                    createTables(connection);
                    tablesCreated = true;
                }
                
                initializing = false;
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            initializing = false;
            return null;
        }
    }

    private static void createTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // First check database version/schema
            boolean needsUpgrade = false;
            try {
                stmt.execute("SELECT price FROM order_items LIMIT 1");
            } catch (SQLException e) {
                needsUpgrade = true;
                System.out.println("Database schema needs upgrade - will recreate tables with correct structure");
            }
            
            if (needsUpgrade) {
                // Drop tables that need schema changes, in reverse dependency order
                stmt.execute("DROP TABLE IF EXISTS order_items");
                stmt.execute("DROP TABLE IF EXISTS orders");
            }

            // Users table
            stmt.execute("""
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
            """);

            // Menu table
            stmt.execute("""
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
            """);

            // Orders table
            stmt.execute("""
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
            """);

            // Order items table - This has the schema problem
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS order_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_id INTEGER NOT NULL,
                    item_id INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    price REAL NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders(order_id),
                    FOREIGN KEY (item_id) REFERENCES menu(id)
                )
            """);

            // Cart table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS cart (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    item_id INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    special_instructions TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (item_id) REFERENCES menu(id)
                )
            """);

            // Categories table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    description TEXT,
                    image_path TEXT
                )
            """);

            // Favorites table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS favorites (
                    user_id INTEGER NOT NULL,
                    item_id INTEGER NOT NULL,
                    PRIMARY KEY (user_id, item_id),
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (item_id) REFERENCES menu(id)
                )
            """);

            // Promotions table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS promotions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    code TEXT NOT NULL UNIQUE,
                    description TEXT,
                    discount_percentage REAL NOT NULL,
                    min_order_amount REAL,
                    start_date TIMESTAMP,
                    end_date TIMESTAMP,
                    is_active INTEGER DEFAULT 1
                )
            """);

            // Create default admin if not exists
            String checkAdmin = "SELECT count(*) FROM users WHERE username = 'admin'";
            ResultSet rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                String insertAdmin = """
                    INSERT INTO users (username, password, role, full_name)
                    VALUES ('admin', 'admin123', 'Admin', 'System Administrator')
                """;
                stmt.execute(insertAdmin);
            }

            // Create default categories if not exists
            String checkCategories = "SELECT count(*) FROM categories";
            rs = stmt.executeQuery(checkCategories);
            if (rs.next() && rs.getInt(1) == 0) {
                String[] defaultCategories = {
                    "Pizza", "Burgers", "Salads", "Pastas", "Desserts", "Beverages"
                };
                for (String category : defaultCategories) {
                    String insertCategory = "INSERT INTO categories (name) VALUES ('" + category + "')";
                    stmt.execute(insertCategory);
                }
            }
            
            // Make sure product_images directory exists
            File imagesDir = new File("product_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdir();
            }

            System.out.println("Database tables created/verified successfully");
        } catch (SQLException e) {
            System.err.println("Error creating database tables: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    // Add shutdown hook to close connection when application exits
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            closeConnection();
        }));
    }
} 