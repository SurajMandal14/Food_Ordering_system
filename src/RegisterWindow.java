package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RegisterWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;
    private MainWindow mainWindow;

    public RegisterWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setTitle("Food Ordering System - Register");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(mainWindow);

        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 250));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title label with improved styling
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setMargin(new Insets(5, 5, 5, 5));
        formPanel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMargin(new Insets(5, 5, 5, 5));
        formPanel.add(passwordField, gbc);

        // Confirm Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(confirmLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setMargin(new Insets(5, 5, 5, 5));
        formPanel.add(confirmPasswordField, gbc);

        // Full Name field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fullNameField.setMargin(new Insets(5, 5, 5, 5));
        formPanel.add(fullNameField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setMargin(new Insets(5, 5, 5, 5));
        formPanel.add(emailField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneField.setMargin(new Insets(5, 5, 5, 5));
        formPanel.add(phoneField, gbc);

        // Address field
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        addressField = new JTextField(20);
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressField.setMargin(new Insets(5, 5, 5, 5));
        formPanel.add(addressField, gbc);

        // Message label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.RED);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(245, 245, 250));
        
        // Create buttons with custom styling
        registerButton = new JButton("Create Account");
        backButton = new JButton("Back to Login");
        
        // Style the buttons
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(150, 40));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        registerButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(80, 200, 120));
            }
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(60, 179, 113));
            }
        });
        
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(new Color(100, 150, 200));
            }
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(new Color(70, 130, 180));
            }
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        // Add components to main panel with proper spacing
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(245, 245, 250));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(new Color(245, 245, 250));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(245, 245, 250));
        southPanel.add(messagePanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // Add action listeners
        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> {
            this.dispose();
            mainWindow.setVisible(true);
        });

        // Add Enter key listener to confirm password field
        confirmPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleRegistration();
                }
            }
        });

        // Set focus to username field
        usernameField.requestFocusInWindow();

        add(mainPanel);
        pack();
        setMinimumSize(new Dimension(450, 650));
        setResizable(false);
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocusInWindow();
            return;
        }

        if (User.register(username, password, fullName, email, phone, address)) {
            JOptionPane.showMessageDialog(this, 
                "Registration successful! Please login.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            mainWindow.setVisible(true);
        } else {
            messageLabel.setText("Registration failed. Username may already exist.");
        }
    }
} 