package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JCheckBox adminModeCheckBox;
    private JLabel messageLabel;

    public MainWindow() {
        setTitle("Food Ordering System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);
        
        // Main panel with background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 250));
        
        // Top panel with food image
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(new Color(245, 245, 250));
        JLabel imageLabel = new JLabel(new ImageIcon("product_images/food_banner.png"));
        JLabel titleLabel = new JLabel("Food Ordering System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        imagePanel.add(titleLabel, BorderLayout.NORTH);
        try {
            // Try to add image if available
            ImageIcon icon = new ImageIcon("product_images/food_banner.png");
            if (icon.getIconWidth() > 0) {
                imageLabel.setIcon(icon);
                imagePanel.add(imageLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            // If image not available, use text
            JPanel colorPanel = new JPanel();
            colorPanel.setBackground(new Color(70, 130, 180));
            colorPanel.setPreferredSize(new Dimension(400, 100));
            imagePanel.add(colorPanel, BorderLayout.CENTER);
        }
        imagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(imagePanel, BorderLayout.NORTH);

        // Center panel for login form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        // Create form components with improved styling
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setMargin(new Insets(5, 5, 5, 5));
        
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMargin(new Insets(5, 5, 5, 5));
        
        adminModeCheckBox = new JCheckBox("Admin Mode");
        adminModeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adminModeCheckBox.setBackground(new Color(245, 245, 250));
        
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel loginLabel = new JLabel("Sign In", SwingConstants.CENTER);
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        formPanel.add(loginLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(adminModeCheckBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(messageLabel, gbc);
        
        // Buttons with better styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 250));
        
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(120, 40));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effects
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(100, 150, 200));
            }
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(70, 130, 180));
            }
        });
        
        registerButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(80, 200, 120));
            }
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(60, 179, 113));
            }
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 10, 8, 10);
        formPanel.add(buttonPanel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(245, 245, 250));
        JLabel footerLabel = new JLabel("© 2023 Food Ordering System", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add event handlers
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegisterWindow());
        
        // Add Enter key listener to password field
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
        
        // Set focus to username field
        usernameField.requestFocusInWindow();
        
        // Add panel to frame with padding
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        containerPanel.add(mainPanel);
        
        add(containerPanel);
        pack();
        setMinimumSize(new Dimension(450, 500));
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password");
            return;
        }
        
        User user = User.authenticate(username, password);
        if (user != null) {
            if (adminModeCheckBox.isSelected() && user.getRole().equals("Admin")) {
                new AdminWindow(user).setVisible(true);
                this.dispose();
            } else if (!adminModeCheckBox.isSelected()) {
                new CustomerWindow(user).setVisible(true);
                this.dispose();
            } else {
                messageLabel.setText("You don't have admin privileges");
                passwordField.setText("");
            }
        } else {
            messageLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
    }
    
    private void openRegisterWindow() {
        new RegisterWindow(this).setVisible(true);
        this.setVisible(false);
    }
    
    public void openLoginWindow() {
        new LoginWindow(this).setVisible(true);
        this.setVisible(false);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new MainWindow().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
} 