package src;

import java.sql.*;

public class User {
    private int id;
    private String username;
    private String role;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String joinDate;

    public User(int id, String username, String role, String fullName, 
                String email, String phone, String address, String joinDate) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.joinDate = joinDate;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getJoinDate() { return joinDate; }

    public static User authenticate(String username, String password) {
        String sql = """
            SELECT id, username, role, full_name, email, phone, address, 
                   datetime(join_date, 'localtime') as join_date 
            FROM users 
            WHERE username = ? AND password = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("join_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }

    public static boolean register(String username, String password, String fullName,
                                 String email, String phone, String address) {
        String sql = """
            INSERT INTO users (username, password, role, full_name, email, phone, address)
            VALUES (?, ?, 'Customer', ?, ?, ?, ?)
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setString(6, address);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateProfile(int userId, String fullName, String email,
                                      String phone, String address) {
        String sql = """
            UPDATE users 
            SET full_name = ?, email = ?, phone = ?, address = ?
            WHERE id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.setInt(5, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }

    public static boolean changePassword(int userId, String oldPassword, String newPassword) {
        String sql = """
            UPDATE users 
            SET password = ?
            WHERE id = ? AND password = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            pstmt.setString(3, oldPassword);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
} 