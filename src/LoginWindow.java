package src;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private MainWindow mainWindow;

    public LoginWindow() {
        this(null);
    }
    
    public LoginWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setTitle("Food Ordering System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);  // Increased height
        setLocationRelativeTo(null);

        // Main panel with GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Welcome to Food Ordering System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 20, 5);
        mainPanel.add(titleLabel, gbc);

        // Reset insets for other components
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(usernameLabel, gbc);

        // Username field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setMargin(new Insets(5, 5, 5, 5));
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(usernameField, gbc);

        // Password label
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(passwordLabel, gbc);

        // Password field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMargin(new Insets(5, 5, 5, 5));
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(passwordField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 250));

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Style buttons
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.black);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.black);
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

        // Add button panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 20, 5);
        mainPanel.add(buttonPanel, gbc);

        // Message label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(messageLabel, gbc);

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegisterWindow());

        // Add Enter key listener
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

        // Add main panel to frame with some padding
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapperPanel.setBackground(new Color(245, 245, 250));
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);
        
        add(wrapperPanel);
        
        setResizable(false);
        pack();
        setMinimumSize(new Dimension(400, 350));
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
            if (user.getRole().equals("Admin")) {
                new AdminWindow(user).setVisible(true);
            } else {
                new CustomerWindow(user).setVisible(true);
            }
            // Close the login window
            this.dispose();
            // If we were opened from MainWindow, close it too
            if (mainWindow != null) {
                mainWindow.dispose();
            }
        } else {
            messageLabel.setText("Invalid username or password");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    private void openRegisterWindow() {
        if (mainWindow != null) {
            // Use the existing MainWindow to open RegisterWindow
            new RegisterWindow(mainWindow).setVisible(true);
            this.dispose();
        } else {
            // Create a new MainWindow and open RegisterWindow from it
            MainWindow newMainWindow = new MainWindow();
            new RegisterWindow(newMainWindow).setVisible(true);
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginWindow().setVisible(true);
        });
    }
} 