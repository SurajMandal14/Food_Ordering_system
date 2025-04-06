package src;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Order {
    private int orderId;
    private int userId;
    private String customerName;
    private String orderStatus;
    private String orderDate;
    private String deliveryAddress;
    private String specialInstructions;
    private String paymentMethod;
    private String paymentStatus;
    private double totalAmount;
    private String estimatedDeliveryTime;
    private String actualDeliveryTime;
    private Integer rating;
    private String feedback;

    public Order(int orderId, int userId, String customerName, String orderStatus, String orderDate,
                String deliveryAddress, String specialInstructions, String paymentMethod,
                String paymentStatus, double totalAmount, String estimatedDeliveryTime,
                String actualDeliveryTime, Integer rating, String feedback) {
        this.orderId = orderId;
        this.userId = userId;
        this.customerName = customerName;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.deliveryAddress = deliveryAddress;
        this.specialInstructions = specialInstructions;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.totalAmount = totalAmount;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.actualDeliveryTime = actualDeliveryTime;
        this.rating = rating;
        this.feedback = feedback;
    }

    // Simple constructor with minimal fields
    public Order(int orderId, int userId, String customerName, String orderStatus, String orderDate) {
        this(orderId, userId, customerName, orderStatus, orderDate, "", "", "Cash", "Pending", 0.0, null, null, 0, "");
    }

    // Getters
    public int getOrderId() { return orderId; }
    public int getUserId() { return userId; }
    public String getCustomerName() { return customerName; }
    public String getOrderStatus() { return orderStatus; }
    public String getOrderDate() { return orderDate; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getSpecialInstructions() { return specialInstructions; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public double getTotalAmount() { return totalAmount; }
    public String getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public String getActualDeliveryTime() { return actualDeliveryTime; }
    public Integer getRating() { return rating; }
    public String getFeedback() { return feedback; }

    // Inner class for order items
    public static class OrderItem {
        private int itemId;
        private String itemName;
        private int quantity;
        private double price;

        public OrderItem(int itemId, String itemName, int quantity, double price) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters
        public int getItemId() { return itemId; }
        public String getItemName() { return itemName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }

        public double getTotal() {
            return quantity * price;
        }
    }

    // Static methods for database operations
    public static boolean addToCart(int userId, int itemId, int quantity) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Check if item already exists in cart
            String checkSql = "SELECT quantity FROM cart WHERE user_id = ? AND item_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, itemId);
                
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    // Update existing cart item
                    String updateSql = "UPDATE cart SET quantity = quantity + ? WHERE user_id = ? AND item_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, quantity);
                        updateStmt.setInt(2, userId);
                        updateStmt.setInt(3, itemId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Add new cart item
                    String insertSql = "INSERT INTO cart (user_id, item_id, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setInt(2, itemId);
                        insertStmt.setInt(3, quantity);
                        insertStmt.executeUpdate();
                    }
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error adding item to cart: " + e.getMessage());
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static List<OrderItem> getCartItems(int userId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = """
            SELECT c.item_id, m.item_name, c.quantity, m.price
            FROM cart c
            JOIN menu m ON c.item_id = m.id
            WHERE c.user_id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderItem(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cart items: " + e.getMessage());
        }
        return items;
    }

    public static boolean removeFromCart(int userId, int itemId) {
        String sql = "DELETE FROM cart WHERE user_id = ? AND item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, itemId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error removing item from cart: " + e.getMessage());
            return false;
        }
    }

    public static boolean placeOrder(int userId, String deliveryAddress, 
                                   String specialInstructions, String paymentMethod,
                                   double totalAmount) {
        String sql = """
            INSERT INTO orders (user_id, order_status, delivery_address, 
                              special_instructions, payment_method, payment_status, 
                              total_amount)
            VALUES (?, 'Pending', ?, ?, ?, 'Pending', ?)
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, deliveryAddress);
            pstmt.setString(3, specialInstructions);
            pstmt.setString(4, paymentMethod);
            pstmt.setDouble(5, totalAmount);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error placing order: " + e.getMessage());
            return false;
        }
    }

    public static boolean createOrder(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            // Get a fresh connection
            conn = DatabaseConnection.getConnection();
            
            // Begin transaction
            conn.setAutoCommit(false);
            
            // First, get cart items (do this before calculating total in case items change)
            List<OrderItem> cartItems = new ArrayList<>();
            String cartSql = """
                SELECT c.item_id, m.item_name, c.quantity, m.price
                FROM cart c
                JOIN menu m ON c.item_id = m.id
                WHERE c.user_id = ?
            """;
            
            pstmt = conn.prepareStatement(cartSql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            double total = 0.0;
            while (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getInt("item_id"),
                    rs.getString("item_name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price")
                );
                cartItems.add(item);
                total += item.getTotal();
            }
            
            if (cartItems.isEmpty()) {
                System.out.println("No items in cart for user ID: " + userId);
                return false; // No items in cart
            }
            
            // Close resources from first query
            rs.close();
            pstmt.close();
            
            // Insert order
            String orderSql = """
                INSERT INTO orders (user_id, order_status, order_date, total_amount, payment_status) 
                VALUES (?, 'Pending', CURRENT_TIMESTAMP, ?, 'Pending')
            """;
            
            pstmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, total);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Order insert affected " + rowsAffected + " rows");
            
            int orderId = -1;
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
                System.out.println("Order created with ID: " + orderId);
            } else {
                System.out.println("Failed to get order ID from generated keys");
            }
            
            if (orderId == -1) {
                System.out.println("Order ID is -1, rolling back transaction");
                conn.rollback();
                return false;
            }
            
            // Close resources from second query
            rs.close();
            pstmt.close();
            
            // Insert order items - use the correct column name 'price'
            String orderItemSql = "INSERT INTO order_items (order_id, item_id, quantity, price) VALUES (?, ?, ?, ?)";
            
            int itemsInserted = 0;
            for (OrderItem item : cartItems) {
                pstmt = conn.prepareStatement(orderItemSql);
                pstmt.setInt(1, orderId);
                pstmt.setInt(2, item.getItemId());
                pstmt.setInt(3, item.getQuantity());
                pstmt.setDouble(4, item.getPrice());
                itemsInserted += pstmt.executeUpdate();
                pstmt.close();
            }
            
            System.out.println("Inserted " + itemsInserted + " order items");
            
            // Clear the cart
            String clearCartSql = "DELETE FROM cart WHERE user_id = ?";
            pstmt = conn.prepareStatement(clearCartSql);
            pstmt.setInt(1, userId);
            int cartRowsDeleted = pstmt.executeUpdate();
            System.out.println("Deleted " + cartRowsDeleted + " items from cart for user ID: " + userId);
            
            // Commit the transaction
            conn.commit();
            System.out.println("Order transaction committed successfully");
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.rollback();
                    System.out.println("Transaction rolled back due to error");
                }
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                // Close all resources in finally block
                if (rs != null) {
                    try { rs.close(); } catch (SQLException e) { /* ignore */ }
                }
                if (pstmt != null) {
                    try { pstmt.close(); } catch (SQLException e) { /* ignore */ }
                }
                if (conn != null) {
                    try { 
                        conn.setAutoCommit(true);
                        conn.close(); 
                    } catch (SQLException e) { 
                        System.err.println("Error closing connection: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error cleaning up resources: " + e.getMessage());
            }
        }
    }

    public static List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.*, u.full_name
            FROM orders o
            JOIN users u ON o.user_id = u.id
            WHERE o.user_id = ? 
            ORDER BY o.order_date DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("order_status"),
                        rs.getString("order_date"),
                        rs.getString("delivery_address"),
                        rs.getString("special_instructions"),
                        rs.getString("payment_method"),
                        rs.getString("payment_status"),
                        rs.getDouble("total_amount"),
                        rs.getString("estimated_delivery_time"),
                        rs.getString("actual_delivery_time"),
                        rs.getObject("rating") != null ? rs.getInt("rating") : null,
                        rs.getString("feedback")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user orders: " + e.getMessage());
        }
        return orders;
    }

    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = """
            SELECT o.*, u.full_name
            FROM orders o
            JOIN users u ON o.user_id = u.id
            ORDER BY o.order_date DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                orders.add(new Order(
                    rs.getInt("order_id"),
                    rs.getInt("user_id"),
                    rs.getString("full_name"),
                    rs.getString("order_status"),
                    rs.getString("order_date"),
                    rs.getString("delivery_address"),
                    rs.getString("special_instructions"),
                    rs.getString("payment_method"),
                    rs.getString("payment_status"),
                    rs.getDouble("total_amount"),
                    rs.getString("estimated_delivery_time"),
                    rs.getString("actual_delivery_time"),
                    rs.getObject("rating") != null ? rs.getInt("rating") : null,
                    rs.getString("feedback")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
        }
        return orders;
    }

    public static boolean updateOrderStatus(int orderId, String status) {
        String sql = """
            UPDATE orders 
            SET order_status = ?, 
                actual_delivery_time = CASE 
                    WHEN ? = 'Delivered' THEN CURRENT_TIMESTAMP 
                    ELSE actual_delivery_time 
                END
            WHERE order_id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, status);
            pstmt.setInt(3, orderId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }

    public static boolean updatePaymentStatus(int orderId, String status) {
        String sql = "UPDATE orders SET payment_status = ? WHERE order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            return false;
        }
    }

    public static boolean addRatingAndFeedback(int orderId, int rating, String feedback) {
        String sql = "UPDATE orders SET rating = ?, feedback = ? WHERE order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rating);
            pstmt.setString(2, feedback);
            pstmt.setInt(3, orderId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding rating and feedback: " + e.getMessage());
            return false;
        }
    }

    private static boolean isAdmin(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT role FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return "Admin".equals(rs.getString("role"));
            }
        } catch (SQLException e) {
            System.err.println("Error checking admin status: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database resources: " + e.getMessage());
            }
        }
        return false;
    }
} 