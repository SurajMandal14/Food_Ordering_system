package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    // Add an item to the cart
    public static boolean addToCart(int userId, int itemId, int quantity) {
        // Check if item already exists in cart
        String checkSql = "SELECT quantity FROM cart WHERE user_id = ? AND item_id = ?";
        String insertSql = "INSERT INTO cart (user_id, item_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE cart SET quantity = quantity + ? WHERE user_id = ? AND item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if item exists in cart
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, itemId);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Item exists, update quantity
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, quantity);
                            updateStmt.setInt(2, userId);
                            updateStmt.setInt(3, itemId);
                            return updateStmt.executeUpdate() > 0;
                        }
                    } else {
                        // Item doesn't exist, insert new record
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setInt(2, itemId);
                            insertStmt.setInt(3, quantity);
                            return insertStmt.executeUpdate() > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
        }
        return false;
    }
    
    // Update the quantity of an item in the cart
    public static boolean updateCartItemQuantity(int userId, int itemId, int quantity) {
        String sql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, itemId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating cart item: " + e.getMessage());
        }
        return false;
    }
    
    // Remove an item from the cart
    public static boolean removeFromCart(int userId, int itemId) {
        String sql = "DELETE FROM cart WHERE user_id = ? AND item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, itemId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error removing item from cart: " + e.getMessage());
        }
        return false;
    }
    
    // Get all items in a user's cart
    public static List<CartItem> getCartItems(int userId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT * FROM cart WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem(
                        rs.getInt("user_id"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getString("special_instructions") != null ? 
                            rs.getString("special_instructions") : ""
                    );
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting cart items: " + e.getMessage());
        }
        return items;
    }
    
    // Clear the entire cart for a user
    public static boolean clearCart(int userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
        }
        return false;
    }
    
    // Calculate the total price of items in the cart
    public static double calculateCartTotal(int userId) {
        double total = 0.0;
        String sql = """
            SELECT SUM(m.price * c.quantity) as total 
            FROM cart c 
            JOIN menu m ON c.item_id = m.id 
            WHERE c.user_id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating cart total: " + e.getMessage());
        }
        return total;
    }
} 