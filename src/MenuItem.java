package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;

public class MenuItem {
    private int id;
    private String itemName;
    private String description;
    private double price;
    private String category;
    private String imagePath;
    private boolean isVegetarian;
    private boolean isSpicy;
    private int calories;
    private int preparationTime;
    private boolean isAvailable;

    public MenuItem(int id, String itemName, String description, double price, 
                   String category, String imagePath, boolean isVegetarian, 
                   boolean isSpicy, int calories, int preparationTime, boolean isAvailable) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
        this.isVegetarian = isVegetarian;
        this.isSpicy = isSpicy;
        this.calories = calories;
        this.preparationTime = preparationTime;
        this.isAvailable = isAvailable;
    }

    // Getters
    public int getId() { return id; }
    public String getItemName() { return itemName; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImagePath() { return imagePath; }
    public boolean isVegetarian() { return isVegetarian; }
    public boolean isSpicy() { return isSpicy; }
    public int getCalories() { return calories; }
    public int getPreparationTime() { return preparationTime; }
    public boolean isAvailable() { return isAvailable; }

    // Static methods for database operations
    public static List<MenuItem> getAllItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu WHERE is_available = 1 ORDER BY category, item_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new MenuItem(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("image_path"),
                        rs.getBoolean("is_vegetarian"),
                        rs.getBoolean("is_spicy"),
                        rs.getInt("calories"),
                        rs.getInt("preparation_time"),
                        rs.getBoolean("is_available")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching menu items: " + e.getMessage());
        }
        return items;
    }

    public static MenuItem getItemById(int itemId) {
        String sql = "SELECT * FROM menu WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new MenuItem(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("image_path"),
                        rs.getBoolean("is_vegetarian"),
                        rs.getBoolean("is_spicy"),
                        rs.getInt("calories"),
                        rs.getInt("preparation_time"),
                        rs.getBoolean("is_available")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching menu item by ID: " + e.getMessage());
        }
        return null;
    }

    public static List<MenuItem> getItemsByCategory(String category) {
        List<MenuItem> items = new ArrayList<>();
        String sql = "SELECT * FROM menu WHERE category = ? AND is_available = 1 ORDER BY item_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new MenuItem(
                        rs.getInt("id"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("image_path"),
                        rs.getBoolean("is_vegetarian"),
                        rs.getBoolean("is_spicy"),
                        rs.getInt("calories"),
                        rs.getInt("preparation_time"),
                        rs.getBoolean("is_available")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching menu items by category: " + e.getMessage());
        }
        return items;
    }

    public static boolean addItem(String itemName, String description, double price,
                                String category, String imagePath, boolean isVegetarian,
                                boolean isSpicy, int calories, int preparationTime) {
        String sql = """
            INSERT INTO menu (item_name, description, price, category, image_path, 
                            is_vegetarian, is_spicy, calories, preparation_time, is_available)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemName);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setString(4, category);
            pstmt.setString(5, imagePath);
            pstmt.setBoolean(6, isVegetarian);
            pstmt.setBoolean(7, isSpicy);
            pstmt.setInt(8, calories);
            pstmt.setInt(9, preparationTime);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding menu item: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateItem(int id, String itemName, String description,
                                   double price, String category, String imagePath,
                                   boolean isVegetarian, boolean isSpicy,
                                   int calories, int preparationTime, boolean isAvailable) {
        String sql = """
            UPDATE menu 
            SET item_name = ?, description = ?, price = ?, category = ?, 
                image_path = ?, is_vegetarian = ?, is_spicy = ?, 
                calories = ?, preparation_time = ?, is_available = ?
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemName);
            pstmt.setString(2, description);
            pstmt.setDouble(3, price);
            pstmt.setString(4, category);
            pstmt.setString(5, imagePath);
            pstmt.setBoolean(6, isVegetarian);
            pstmt.setBoolean(7, isSpicy);
            pstmt.setInt(8, calories);
            pstmt.setInt(9, preparationTime);
            pstmt.setBoolean(10, isAvailable);
            pstmt.setInt(11, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating menu item: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteItem(int id) {
        // First get the image path
        String imagePath = null;
        String selectSql = "SELECT image_path FROM menu WHERE id = ?";
        String deleteSql = "DELETE FROM menu WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Get image path
            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    imagePath = rs.getString("image_path");
                }
            }
            
            // Delete menu item
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, id);
                if (pstmt.executeUpdate() > 0) {
                    // Delete image file if exists
                    if (imagePath != null && !imagePath.isEmpty()) {
                        ImageHandler.deleteImage(imagePath);
                    }
                    conn.commit();
                    return true;
                }
            }
            
            conn.rollback();
            return false;
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error deleting menu item: " + e.getMessage());
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

    private String getFormattedImagePath() {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        // Convert relative path to absolute if needed
        File imageFile = new File(imagePath);
        if (!imageFile.isAbsolute()) {
            imageFile = new File(System.getProperty("user.dir"), imagePath);
        }
        return imageFile.getAbsolutePath();
    }

    public ImageIcon getImageIcon(int width, int height) {
        String path = getFormattedImagePath();
        if (path == null) {
            // Return default image
            return createDefaultImage(width, height);
        }

        try {
            BufferedImage img = ImageIO.read(new File(path));
            if (img != null) {
                Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            }
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
        }
        return createDefaultImage(width, height);
    }

    private ImageIcon createDefaultImage(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Set background
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, width, height);
        
        // Draw border
        g2d.setColor(Color.GRAY);
        g2d.drawRect(0, 0, width - 1, height - 1);
        
        // Draw "No Image" text
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "No Image";
        int textX = (width - fm.stringWidth(text)) / 2;
        int textY = (height + fm.getAscent()) / 2;
        g2d.drawString(text, textX, textY);
        
        g2d.dispose();
        return new ImageIcon(img);
    }
} 